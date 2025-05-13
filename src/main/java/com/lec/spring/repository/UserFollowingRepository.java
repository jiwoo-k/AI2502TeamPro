package com.lec.spring.repository;

import com.lec.spring.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserFollowingRepository {


    // 새로운 팔로우 관계를 추가합니다.
    int insert(UserFollowing userFollowing);


    // 지정한 팔로우 관계를 조회합니다.
    UserFollowing findById(Long followingUserId, Long followedUserId);


    // 내가 follow 한 모든 관계를 조회합니다.
    List<UserFollowing> findByFollowingUserId(Long followingUserId);


    // 특정 사용자를 팔로우하는(팔로워인) 모든 관계를 조회합니다.
    List<UserFollowing> findByFollowedUserId(Long followedUserId);


    // 지정한 팔로우 관계를 삭제합니다.
    int delete(UserFollowing userFollowing);
}
