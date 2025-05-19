package com.lec.spring.service;

import com.lec.spring.domain.Category;
import com.lec.spring.repository.CategoryRepository;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(SqlSession sqlSession) {
        this.categoryRepository = sqlSession.getMapper(CategoryRepository.class);
    }

    @Override
    public int save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public int update(Category category) {
        return categoryRepository.update(category);
    }

    @Override
    public int delete(Long id) {
        return categoryRepository.delete(id);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> list() {
        return categoryRepository.list();
    }
}
