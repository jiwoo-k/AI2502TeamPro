package com.lec.spring.domain;

import com.lec.spring.repository.TagRepository;
import com.lec.spring.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class TagValidator implements Validator {

    private final TagRepository tagRepository;
    private final CategoryService categoryService;

    public TagValidator(CategoryService categoryService, TagRepository tagRepository) {
        this.tagRepository = tagRepository;
        this.categoryService = categoryService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        System.out.println("supports(" + clazz.getName() + ")");
        boolean result = Tag.class.isAssignableFrom(clazz);
        System.out.println(result);
        return result;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Tag tag = (Tag) target;
        Long id = tag.getCategory_id();
        String tagName = tag.getName();

        //1. 카테고리 선택 안했을 시
        if(id == null){
            errors.rejectValue("category_id", "카테고리를 선택해주세요.");
        }

        //2. 태그명 입력안했을 시
        if(tagName == null || tagName.trim().isEmpty()){
            errors.rejectValue("name", "검색어를 입력해주세요.");
        }

        //3. 검색된 태그가 없을 시..
        Tag searchedTag = tagRepository.searchTag(tag);
        if(searchedTag == null && (id != null && !tagName.trim().isEmpty())){
            errors.rejectValue("id", "기존에 없던 태그입니다. 추가해서 사용하세요.");
        }
    }
}
