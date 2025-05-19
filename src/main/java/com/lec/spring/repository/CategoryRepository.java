package com.lec.spring.repository;

import com.lec.spring.domain.Category;

import java.util.List;

public interface CategoryRepository {
    //1. 새 카테고리 저장
    int save(Category category);

    //2. id 로 특정 카테고리 찾기
    Category findById(Long id);

    //3. name 으로 특정 카테고리 찾기
    Category findByName(String name);

    //4. 특정 카테고리 정보 수정
    int update(Category category);

    //5. 특정 카테고리 삭제
    int delete(Long id);

    //6. 카테고리 전체 목록 + 태그 개수 불러오기
    List<Category> list();
}
