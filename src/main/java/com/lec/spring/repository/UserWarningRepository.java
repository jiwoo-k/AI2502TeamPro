package com.lec.spring.repository;

import com.lec.spring.domain.UserWarning;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserWarningRepository {


    // 새로운 신고 정보를 저장합니다.
    int insert(UserWarning warning);


    // 특정 게시물에 대한 신고 정보들을 조회합니다.
    List<UserWarning> findByPostId(Long postId);


    // 특정 사용자가 신고한 신고 정보들을 조회합니다.
    List<UserWarning> findByComplaintUserId(Long complaintUserId);
}
