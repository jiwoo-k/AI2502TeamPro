package com.lec.spring.repository;

import com.lec.spring.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRepository {
    //1. 회원가입 시 사용자 저장
    int save(User user);

    //2. id 로 특정 사용자 찾기
    User findById(Long id);

    //3. username 으로 특정 사용자 찾기
    User findByUsername(String username);

    //4. name 으로 특정 사용자 찾기
    User findByName(String name);

    //5. 특정 사용자 정보 수정
    int update(User user);

    //6. 해당 사용자(id) 찾아 로그인 history 저장
    int saveLogin(Long id);

    //7. 사용자 위치 정보 받기
    int updateLocation(User user);

    //8. 게시물을 쓴 사용자들 목록 가져오기
    List<User> findNearUsers();
}
