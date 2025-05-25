package com.lec.spring.service;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;

import java.util.List;

public interface UserService {

    // 유저 id 가지고 오기 (팔로우 땜에 필요)
    User findByUserId(Long id);

    // username(회원 아이디) 의 User 정보 읽어오기
    User findByUsername(String username);

    //name(닉네임) 의 User 정보 읽어오기
    User findByName(String name);

    // 특정 username(회원 아이디) 의 회원이 존재하는지 확인
    boolean isExistUserName(String username);

    // 특정 name(회원 아이디) 의 회원이 존재하는지 확인
    boolean isExistName(String name);

    // 신규 회원 등록
    int register(User user);

    // 특정 사용자(id)의 authority(들)
    List<Authority> selectAuthoritiesByUserId(Long id);

    int saveUserLoginHistory(Long id);

    int updateLocation(User user);

    List<User> findNearUsers();

    //사용자들 신고 목록
    List<User> findUsersByWarnCount(Integer warnCount1, Integer warnCount2);

    //특정 사용자 특정 일수 만큼 계정 정지
    void limitUser(Long id, Integer days);
}












