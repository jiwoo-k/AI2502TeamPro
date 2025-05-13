package com.lec.spring.repository;

import com.lec.spring.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentRepository {

    List<Comment> findByPostId(Long postId);
    int save(Comment comment); // 또는 insert
    Comment findById(Long id);
    int update(Comment comment);
    int delete(Long id);
    int deleteByParentId(Long parentId);


    int updateIsPicked(@Param("id") Long id, @Param("isPicked") Boolean isPicked);

    // (추가 메소드 고려)
    // - 특정 사용자가 작성한 댓글 조회 (마이페이지)
    // - 특정 게시글에 달린 최상위 댓글만 조회
    // - 특정 부모 댓글에 달린 답글만 조회
    // - 삭제된 댓글 처리 (is_deleted 플래그 등 사용 시)
}