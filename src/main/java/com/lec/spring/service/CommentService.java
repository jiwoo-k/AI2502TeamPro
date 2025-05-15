package com.lec.spring.service;

import com.lec.spring.domain.Comment;
import com.lec.spring.domain.QryCommentList;
import com.lec.spring.domain.QryResult;

import java.util.List;

public interface CommentService {

    QryCommentList list(Long postId);

    int write(Long userId, Comment comment);

    int update(Long userId, Long commentId, String content);

    int delete(Long userId, Long commentId);

    int pickComment(Long userId, Long commentId);

    int unpickComment(Long userId, Long commentId);

    List<Comment> findByUserId(Long userId);
}
