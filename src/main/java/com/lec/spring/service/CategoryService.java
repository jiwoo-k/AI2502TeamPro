package com.lec.spring.service;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.Category;
import com.lec.spring.domain.User;

import java.util.List;

public interface CategoryService {

    int save (Category category);

    int update(Category category);

    int delete (Long id);

    Category findById(Long id);

    Category findByName(String name);

    List<Category> list();






}
