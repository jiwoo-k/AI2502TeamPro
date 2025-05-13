package com.lec.spring.service;

import com.lec.spring.domain.Comment;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.User;
import com.lec.spring.repository.CommentRepository;
import com.lec.spring.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors; // Collectors 사용 시 필요

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional(readOnly = true) // 조회 작업
    public List<Comment> list(Long postId) {
        // 1. Repository 에서 해당 게시글의 모든 댓글 목록 가져오기 (최상위 + 답글 섞여 있음)
        List<Comment> allComments = commentRepository.findByPostId(postId);

        // 2. 가져온 리스트를 2단계 계층 구조 (최상위 댓글 -> 직속 답글) 로 구성
        List<Comment> topLevelComments = allComments.stream()
                .filter(comment -> comment.getParentId() == null) // parentId가 null 인 댓글이 최상위 댓글
                .collect(Collectors.toList());

        // 각 최상위 댓글에 직속 답글 연결 (2단계 구조만)
        topLevelComments.forEach(topComment -> {
            List<Comment> replies = allComments.stream()
                    // parentId가 null 이 아니면서, 그 값이 현재 최상위 댓글의 ID와 같은 댓글들만 찾기
                    .filter(comment -> comment.getParentId() != null && comment.getParentId().equals(topComment.getId()))
                    .collect(Collectors.toList());
            // 찾은 답글 리스트를 최상위 댓글 객체의 replies 필드에 설정
            topComment.setReplies(replies);
        });

        // 계층 구조로 구성된 최상위 댓글 리스트를 반환
        // 이 리스트의 각 Comment 객체 안에 해당 댓글의 직속 답글들이 replies 리스트로 담겨 있음
        return topLevelComments;
    }

    // 댓글 작성
    @Override
    @Transactional // 쓰기 작업
    public int write(Comment comment) {
        // 1. 댓글 내용 유효성 검사
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }

        // 2. 게시글 존재 여부 확인
        Post existingPost = postRepository.findById(comment.getPostId());
        if (existingPost == null) {
            System.err.println("ERROR: 댓글 작성 실패 - 존재하지 않는 게시글: " + comment.getPostId());
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        // 3. 답글인 경우 부모 댓글 존재 여부 확인 및 (2단계 이상 답글 방지)
        if (comment.getParentId() != null) { // parentId가 null 이 아니면 답글임
            Comment parentComment = commentRepository.findById(comment.getParentId());
            if (parentComment == null) {
                throw new IllegalArgumentException("존재하지 않는 부모 댓글입니다.");
            }

            // 부모 댓글의 parentId가 null 이 아닌 경우 (즉, 부모 댓글이 이미 대댓글인 경우) -> 근데 필요할까? 대댓글에는 답글 버튼이 없음
//            if (parentComment.getParentId() != null) {
//                throw new IllegalArgumentException("대댓글에 답글을 달 수 없습니다. (댓글-대댓글 2단계까지만 허용)");
//            }
        }

        // 4. Repository 를 통해 저장
        return commentRepository.save(comment);
    }

    // 댓글 수정
    @Override
    @Transactional // 쓰기 작업
    public int update(Comment comment, User user) {
        Comment existingComment = commentRepository.findById(comment.getId());
        if (existingComment == null) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }

        // 권한 체크: 댓글 작성자와 수정 요청 사용자가 동일한지
        if (user == null || !existingComment.getUserId().equals(user.getId())) {
            System.err.println("ERROR: 댓글 수정 실패 - 권한 없음. 댓글ID: " + comment.getId() + ", 사용자ID: " + (user != null ? user.getId() : "null"));
            throw new AccessDeniedException("댓글 수정 권한이 없습니다.");
        }

        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }

        existingComment.setContent(comment.getContent()); // 내용만 업데이트
        return commentRepository.update(existingComment);
    }

    // 댓글 삭제
    @Override
    @Transactional // 트랜잭션 내에서 답글 먼저 삭제 후 부모 댓글 삭제
    public int delete(Long id, User user) {
        // 1. 삭제할 댓글 존재 여부 확인
        Comment existingComment = commentRepository.findById(id);
        if (existingComment == null) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }

        // 2. 권한 체크: 댓글 작성자 또는 관리자인지 확인
        boolean isAdmin = user != null && user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (user == null || (!existingComment.getUserId().equals(user.getId()) && !isAdmin)) {
            System.err.println("ERROR: 댓글 삭제 실패 - 권한 없음. 댓글ID: " + id + ", 사용자ID: " + (user != null ? user.getId() : "null"));
            throw new AccessDeniedException("댓글 삭제 권한이 없습니다.");
        }

        // 3. 해당 댓글에 달린 모든 답글 먼저 삭제 (2단계까지만 허용하므로 직속 자식들만 삭제하면 됨)
        //    deleteByParentId 메소드는 parent_id가 id인 모든 댓글을 삭제하므로,
        //    2단계까지만 허용되는 구조에서는 해당 댓글의 직속 대댓글들만 삭제됩니다.
        int deletedRepliesCount = commentRepository.deleteByParentId(id);
        System.out.println("댓글 ID " + id + "의 답글 " + deletedRepliesCount + "개 삭제 완료."); // 확인용 로그

        // 4. 부모 댓글 삭제
        int deletedCommentCount = commentRepository.delete(id);

        // 총 삭제된 댓글 수 (본 댓글 + 답글)를 반환할 수 있습니다.
        return deletedRepliesCount + deletedCommentCount; // 총 삭제 개수 반환
    }

    @Override
    @Transactional
    public int pickComment(Long id, Boolean isPicked, Long postAuthorUserId) {
        Comment existingComment = commentRepository.findById(id);
        if (existingComment == null) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }

        // 권한 체크: 게시글 작성자만 픽 가능
        if (postAuthorUserId == null || !existingComment.getPostId().equals(postAuthorUserId)) {
            System.err.println("ERROR: 댓글 픽 실패 - 권한 없음. 댓글ID: " + id + ", 게시글작성자ID: " + postAuthorUserId);
             throw new AccessDeniedException("댓글 픽 권한이 없습니다. (게시글 작성자만 가능)");
        }


        // [미구현] 손님 게시글의 경우 1개의 댓글만 픽 가능 로직 추가 필요
        // - 해당 게시글이 손님 게시글인지 확인 (PostRepository 필요)
        // - 해당 게시글에 이미 픽된 다른 댓글이 있는지 확인 (CommentRepository 에 메소드 추가 필요)
        // - 있다면 기존 픽을 해제하는 로직 수행

        return commentRepository.updateIsPicked(id, isPicked);
    }

}