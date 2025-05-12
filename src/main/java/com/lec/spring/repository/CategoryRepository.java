package com.lec.spring.repository;

import com.lec.spring.domain.Category;

public interface CategoryRepository {
    //1. 회원가입 시 사용자 저장
    int save(Category category);

    //2. id 로 특정 사용자 찾기
    Category findCategoryById(Long id);

    //3. username 으로 특정 사용자 찾기
    Category findCategoryByName(String name);

    //4. 특정 사용자 정보 수정
    int update(Category category);
}
