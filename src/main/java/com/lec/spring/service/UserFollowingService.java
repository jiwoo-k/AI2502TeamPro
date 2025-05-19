package com.lec.spring.service;

import com.lec.spring.domain.User;
import com.lec.spring.domain.UserFollowing;
import java.util.List;

public interface UserFollowingService {


    // 두 사용자의 팔로우 관계를 설정합니다.
    int follow(User followingUser, User followedUser);


    // 두 사용자의 팔로우 관계를 취소합니다.
    int unfollow(User followingUser, User followedUser);


    // 내가 follow 한 모든 관계를 조회합니다.
    List<UserFollowing> getFollowingList(Long followingUserId);


    // 특정 사용자를 팔로우하는(즉, 팔로워인) 모든 관계를 조회합니다.
    List<UserFollowing> getFollowersList(Long followedUserId);

    // 이 사람을 내가 팔로우 중인지 아닌지 확인
    Boolean isFollowing (Long followingUserId, Long followedUserId);

    // 팔로우 몇명?
    int followCount(Long followingUserId);
}