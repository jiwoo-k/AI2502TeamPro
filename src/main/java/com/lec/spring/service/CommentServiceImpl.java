package com.lec.spring.service;

import com.lec.spring.domain.Comment;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.QryCommentList;
import com.lec.spring.repository.CommentRepository;
import com.lec.spring.repository.PostRepository;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(SqlSession sqlSession) {
        this.commentRepository = sqlSession.getMapper(CommentRepository.class);
        this.postRepository = sqlSession.getMapper(PostRepository.class);
    }

    @Override
    @Transactional(readOnly = true)
    public QryCommentList list(Long postId) {
        List<Comment> allComments = commentRepository.findByPostId(postId);

        List<Comment> topLevel = allComments.stream()
                .filter(c -> c.getParentId() == null)
                .collect(Collectors.toList());

        topLevel.forEach(parent -> {
            List<Comment> replies = allComments.stream()
                    .filter(c -> c.getParentId() != null && c.getParentId().equals(parent.getId()))
                    .collect(Collectors.toList());
            parent.setReplies(replies);
        });

        return QryCommentList.builder()
                .count(topLevel.size())
                .status("OK")
                .message("댓글 조회 성공")
                .list(topLevel)
                .build();
    }

    @Override
    @Transactional
    public int write(Long userId, Comment comment) {
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }

        Post post = postRepository.findById(comment.getPostId());
        if (post == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        if (comment.getParentId() != null) {
            Comment parentComment = commentRepository.findById(comment.getParentId());
            if (parentComment == null) {
                throw new IllegalArgumentException("존재하지 않는 부모 댓글입니다.");
            }
            if (parentComment.getParentId() != null) {
                throw new IllegalArgumentException("대댓글에 답글을 달 수 없습니다.");
            }
        }

        comment.setUserId(userId);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public int update(Long userId, Long commentId, String content) {
        Comment existingComment = commentRepository.findById(commentId);
        if (existingComment == null) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }

        if (!existingComment.getUserId().equals(userId)) {
            throw new AccessDeniedException("댓글 수정 권한이 없습니다.");
        }

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 비어있을 수 없습니다.");
        }

        existingComment.setContent(content);
        return commentRepository.update(existingComment);
    }

    @Override
    @Transactional
    public int delete(Long userId, Long commentId) {
        Comment existingComment = commentRepository.findById(commentId);
        if (existingComment == null) {
            throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        }

        if (!existingComment.getUserId().equals(userId)) {
            throw new AccessDeniedException("댓글 삭제 권한이 없습니다.");
        }

        int deletedReplies = commentRepository.deleteByParentId(commentId);
        int deletedMain = commentRepository.delete(commentId);
        return deletedReplies + deletedMain;
    }

    @Override
    @Transactional
    public int pickComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) throw new IllegalArgumentException("존재하지 않는 댓글입니다.");

        Post post = postRepository.findById(comment.getPostId());
        if (post == null || post.getUser_id() == null || !post.getUser_id().getId().equals(userId)) {
            throw new AccessDeniedException("댓글 픽 권한이 없습니다. (게시글 작성자만 가능)");
        }

        List<Comment> allComments = commentRepository.findByPostId(comment.getPostId());
        for (Comment c : allComments) {
            if (Boolean.TRUE.equals(c.getIsPicked())) {
                commentRepository.updateIsPicked(c.getId(), false);
            }
        }

        return commentRepository.updateIsPicked(commentId, true);
    }

    @Override
    @Transactional
    public int unpickComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) throw new IllegalArgumentException("존재하지 않는 댓글입니다.");

        Post post = postRepository.findById(comment.getPostId());
        if (post == null || post.getUser_id() == null || !post.getUser_id().getId().equals(userId)) {
            throw new AccessDeniedException("댓글 픽 해제 권한이 없습니다.");
        }

        return commentRepository.updateIsPicked(commentId, false);
    }

    @Override
    public List<Comment> findByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }
}
