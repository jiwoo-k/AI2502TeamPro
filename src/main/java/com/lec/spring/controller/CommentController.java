package com.lec.spring.controller;

import com.lec.spring.domain.Comment;
import com.lec.spring.domain.QryCommentList;
import com.lec.spring.domain.QryResult;
import com.lec.spring.service.CommentService;
import com.lec.spring.util.U;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // [1] 댓글 목록 조회
    @GetMapping("/list/{postId}")
    public ResponseEntity<QryCommentList> list(@PathVariable Long postId) {
        try {
            QryCommentList result = commentService.list(postId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // [2] 댓글 작성
    @PostMapping("/write")
    public ResponseEntity<QryResult> write(
            @RequestParam("postId") Long postId,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam("content") String content) {

        Long userId = U.getUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(QryResult.builder().status("FAIL").message("로그인이 필요합니다.").build());
        }

        Comment comment = Comment.builder()
                .postId(postId)
                .parentId(parentId)
                .content(content)
                .build();

        try {
            int result = commentService.write(userId, comment);
            if (result > 0) {
                return ResponseEntity.ok(QryResult.builder().status("OK").count(result).message("댓글 작성 성공").build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(QryResult.builder().status("FAIL").message("댓글 작성 실패").build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(QryResult.builder().status("FAIL").message("댓글 작성 중 오류 발생").build());
        }
    }

    // [3] 댓글 수정
    @PutMapping("/update/{commentId}")
    public ResponseEntity<QryResult> update(
            @PathVariable Long commentId,
            @RequestParam("content") String content) {

        Long userId = U.getUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(QryResult.builder().status("FAIL").message("로그인이 필요합니다.").build());
        }

        try {
            int result = commentService.update(userId, commentId, content);
            if (result > 0) {
                return ResponseEntity.ok(QryResult.builder().status("OK").count(result).message("댓글 수정 성공").build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(QryResult.builder().status("FAIL").message("댓글 수정 실패").build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(QryResult.builder().status("FAIL").message("댓글 수정 중 오류 발생").build());
        }
    }

    // [4] 댓글 삭제
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<QryResult> delete(@PathVariable Long commentId) {
        Long userId = U.getUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(QryResult.builder().status("FAIL").message("로그인이 필요합니다.").build());
        }

        try {
            int result = commentService.delete(userId, commentId);
            if (result > 0) {
                return ResponseEntity.ok(QryResult.builder().status("OK").count(result).message("댓글 삭제 성공").build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(QryResult.builder().status("FAIL").message("댓글 삭제 실패").build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(QryResult.builder().status("FAIL").message("댓글 삭제 중 오류 발생").build());
        }
    }

    // [5] 댓글 픽
    @PutMapping("/pick/{commentId}")
    public ResponseEntity<QryResult> pick(@PathVariable Long commentId) {
        Long userId = U.getUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(QryResult.builder().status("FAIL").message("로그인이 필요합니다.").build());
        }

        try {
            int result = commentService.pickComment(userId, commentId);
            if (result > 0) {
                return ResponseEntity.ok(QryResult.builder().status("OK").count(result).message("댓글 픽 성공").build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(QryResult.builder().status("FAIL").message("댓글 픽 실패").build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(QryResult.builder().status("FAIL").message("댓글 픽 중 오류 발생").build());
        }
    }

    // [6] 댓글 픽 해제
    @PutMapping("/unpick/{commentId}")
    public ResponseEntity<QryResult> unpick(@PathVariable Long commentId) {
        Long userId = U.getUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(QryResult.builder().status("FAIL").message("로그인이 필요합니다.").build());
        }

        try {
            int result = commentService.unpickComment(userId, commentId);
            if (result > 0) {
                return ResponseEntity.ok(QryResult.builder().status("OK").count(result).message("픽 해제 성공").build());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(QryResult.builder().status("FAIL").message("픽 해제 실패").build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(QryResult.builder().status("FAIL").message("픽 해제 중 오류 발생").build());
        }
    }

    // [7] 마이페이지용 사용자 댓글 목록 조회
    @GetMapping("/list/byUser/{userId}")
    public ResponseEntity<QryCommentList> listByUser(@PathVariable Long userId) {
        try {
            var comments = commentService.findByUserId(userId);
            return ResponseEntity.ok(QryCommentList.builder()
                    .status("OK")
                    .count(comments.size())
                    .message("사용자 댓글 조회 성공")
                    .list(comments)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(QryCommentList.builder().status("FAIL").message("댓글 조회 중 오류").build());
        }
    }
}
