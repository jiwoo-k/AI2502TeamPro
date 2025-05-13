package com.lec.spring.vaildator;


import com.lec.spring.domain.Post;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BoardValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return Post.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Post post = (Post) target;
        if(post.getTitle() == null || post.getTitle().isEmpty()){
            errors.rejectValue("title", "title","제목을 적으세요");
        }
        if (post.getContent() == null || post.getContent().isEmpty()) {
            errors.rejectValue("content", "content","글을 작성하세요");
        }


    }
}
