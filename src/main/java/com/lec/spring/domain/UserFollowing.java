package com.lec.spring.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollowing {
    private Long followingUserId;
    private Long followedUserId;

    /**
     * 팔로우하는 사용자 객체.
     * JSON 변환 시 순환참조를 피하기 위해 @JsonIgnore 처리
     */
    @JsonIgnore
    private User followingUser;

    /**
     * 팔로우 당하는 사용자 객체.
     * JSON 변환 시 순환참조를 피하기 위해 @JsonIgnore 처리
     */
    @JsonIgnore
    private User followedUser;
}