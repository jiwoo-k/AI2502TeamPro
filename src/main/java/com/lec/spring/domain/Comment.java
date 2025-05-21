package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List; // **[추가]** List 사용을 위해 import

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private Long id; // 댓글의 고유 ID (DB: INT, AUTO_INCREMENT)
    private Long userId; // 댓글 작성자 ID (DB: INT) - User 테이블의 id 참조
    private Long postId; // 댓글이 달린 게시글 ID (DB: INT) - Post 테이블의 id 참조
    private Long parentId; // 답글일 경우 부모 댓글 ID (DB: INT NULL) - Comment 테이블의 id 참조 (자기 자신 참조)
    private String content; // 댓글 내용 (DB: TEXT NULL)
    private LocalDateTime createdAt; // 댓글 작성일시 (DB: DATETIME NULL DEFAULT now())
    private Boolean isPicked; // 픽 여부 (DB: BOOLEAN NOT NULL DEFAULT false) - 손님 게시글에서 작성자가 채택했는지 여부

    // 연관 객체 (Join 등을 통해 가져올 수 있는 정보)
    private User user; // 댓글 작성자 정보 (User 객체) - findByPostId 쿼리에서 JOIN 으로 가져올 때 사용

    // 해당 댓글에 달린 답글 목록을 담을 필드
    private List<Comment> replies;
    /** 댓글이 달린 게시글의 제목 */
    private String postTitle;

}