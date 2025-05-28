package com.lec.spring.repository;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthorityRepository {
   //특정 이름의 권한 정보를 조회
    Authority findByName(String name);

    // 특정 사용자(User) 의 권한(들) 읽어오기
    List<Authority> findByUser(User user);


    // 특정 사용자(user_id) 에 권한(auth_id) 추가 (INSERT)
    int addAuthority(Long user_id, Long auth_id);

    // 관리자 이름 가져와서 detail 에 관리자 허용 부분 만들어주기
    List<Long> adminId( );

}