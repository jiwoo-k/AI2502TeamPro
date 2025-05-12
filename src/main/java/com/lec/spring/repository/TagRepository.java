package com.lec.spring.repository;

import com.lec.spring.domain.Tag;

public interface TagRepository {
    //1. 회원가입 시 사용자 저장
    int save(Tag tag);

    //2. id 로 특정 사용자 찾기
    Tag findTagById(Long id);

    //3. username 으로 특정 사용자 찾기
    Tag findTagByName(String name);

    //4. 특정 사용자 정보 수정
    int update(Tag tag);
}
