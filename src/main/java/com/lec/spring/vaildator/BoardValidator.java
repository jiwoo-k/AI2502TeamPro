package com.lec.spring.vaildator;


import com.lec.spring.domain.Post;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BoardValidator implements Validator {

// 일단 title, content 만 vaildator을 만들었음. 나중에 태그 추가 부분이 생기면 tag == null 부분도 추가해야 함
    // 또 다른게 있나? vaildator 추가할게 또 뭐가 있지
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
        if (post.getPost_tag() == null || post.getPost_tag().isEmpty()) {
            errors.rejectValue("post_tag", "post_tag", "태그를 추가해주세요");
        }


    }
}
