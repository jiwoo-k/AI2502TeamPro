package com.lec.spring.repository;

import com.lec.spring.domain.User;

public interface UserRepository {
    //1. 회원가입 시 사용자 저장
    int save(User user);

    //2. id 로 특정 사용자 찾기
    User findById(Long id);

    //3. username 으로 특정 사용자 찾기
    User findByUsername(String username);

    //4. 특정 사용자 정보 수정
    int update(User user);
}
