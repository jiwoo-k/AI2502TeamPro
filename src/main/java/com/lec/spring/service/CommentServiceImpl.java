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
                .peek(parent -> {
                    List<Comment> replies = allComments.stream()
                            .filter(c -> c.getParentId() != null && c.getParentId().equals(parent.getId()))
                            .collect(Collectors.toList());
                    parent.setReplies(replies);
                })
                .collect(Collectors.toList());

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
        if (post == null) throw new IllegalArgumentException("존재하지 않는 게시글입니다.");

        if (comment.getParentId() != null) {
            Comment parent = commentRepository.findById(comment.getParentId());
            if (parent == null) throw new IllegalArgumentException("존재하지 않는 부모 댓글입니다.");
        }

        comment.setUserId(userId);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public int update(Long userId, Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        if (!comment.getUserId().equals(userId)) throw new AccessDeniedException("수정 권한 없음");

        comment.setContent(content);
        return commentRepository.update(comment);
    }

    @Override
    @Transactional
    public int delete(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
        if (!comment.getUserId().equals(userId)) throw new AccessDeniedException("삭제 권한 없음");

        int repliesDeleted = commentRepository.deleteByParentId(commentId);
        int deleted = commentRepository.delete(commentId);
        return repliesDeleted + deleted;
    }

    @Override
    @Transactional
    public int pickComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) throw new IllegalArgumentException("존재하지 않는 댓글입니다.");

        if (comment.getUserId().equals(userId)) {
            throw new AccessDeniedException("자신의 댓글은 픽할 수 없습니다.");
        }

        Post post = postRepository.findById(comment.getPostId());
        if (post == null || post.getUser_id() == null || !post.getUser_id().equals(userId)) {
            throw new AccessDeniedException("픽 권한 없음");
        }

        List<Comment> all = commentRepository.findByPostId(post.getId());
        for (Comment c : all) {
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
        if (post == null || post.getUser_id() == null || !post.getUser_id().equals(userId)) {
            throw new AccessDeniedException("픽 해제 권한 없음");
        }

        return commentRepository.updateIsPicked(commentId, false);
    }

    @Override
    public List<Comment> findByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }
}
