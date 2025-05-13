package com.lec.spring.service;

import com.lec.spring.domain.Comment;
import com.lec.spring.domain.User;

import java.util.List;

public interface CommentService {

    /**
     * 특정 게시글에 달린 댓글 목록을 조회
     */
    List<Comment> list(Long postId);

    /**
     * 새로운 댓글 또는 답글을 작성
     */
    int write(Comment comment);

    /**
     * 특정 댓글의 내용을 수정
     * 작성자만 수정 가능하도록 권한 체크 필요.
     */
    int update(Comment comment, User user);

    /**
     * 특정 댓글을 삭제
     * 작성자 또는 관리자만 삭제 가능하도록 권한 체크 필요.
     */
    int delete(Long id, User user);

    /**
     * 특정 댓글의 픽 여부를 설정/해제
     * 게시글 작성자만 가능하도록 권한 체크 필요.
     * 손님 게시글의 경우 1개의 댓글만 픽 가능하도록 로직 필요.
     */
    int pickComment(Long id, Boolean isPicked, Long postAuthorUserId);

    // (추가 메소드 고려)
    // - 마이페이지용 사용자 작성 댓글 목록 조회
}