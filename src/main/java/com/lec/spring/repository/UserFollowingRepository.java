package com.lec.spring.repository;

import com.lec.spring.domain.User;
import com.lec.spring.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserFollowingRepository {


    // 새로운 팔로우 관계를 추가합니다.
    int insert(UserFollowing userFollowing);


    // 지정한 팔로우 관계를 조회합니다.
    UserFollowing findByFollow(@Param("followingUserId") Long followingUserId, @Param("followedUserId") Long followedUserId);

    // 팔로우 수
    Integer followCount (@Param("followingUserId") Long followingUserId);

    // 내가 follow 한 모든 관계를 조회합니다.
    // 게시판 목록 (list, 도우미 게시판), 게시물 상세조회(detail , 손님, 도우미 게시판)
    List<UserFollowing> findByFollowingUserId(Long followingUserId);


    // 특정 사용자를 팔로우하는(팔로워인) 모든 관계를 조회합니다.
    List<UserFollowing> findByFollowedUserId(Long followedUserId);


    // 지정한 팔로우 관계를 삭제합니다.
    int delete(@Param("followingUserId") Long followingUserId, @Param("followedUserId") Long followedUserId);

    Optional<User> findById(Long id);
}