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
}