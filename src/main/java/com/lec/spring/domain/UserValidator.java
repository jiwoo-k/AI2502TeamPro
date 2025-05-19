package com.lec.spring.domain;

import com.lec.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        System.out.println("supports(" + clazz.getName() + ")");
        // ↓ 검증할 객체의 클래스 타입인지 확인
        boolean result = User.class.isAssignableFrom(clazz);
        System.out.println(result);
        return result;
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        String username = user.getUsername();
        String name = user.getName();
//        String juminNo = user.getJuminNo();

        //username 검증
        if(username == null || username.trim().isEmpty()){
            errors.rejectValue("username", "아이디 입력은 필수입니다");
        } else if(userService.isExistUserName(username)){
            //username 은 중복 불가
            errors.rejectValue("username", "이미 존재하는 아이디입니다");
        }

        // name, password 검증
        if(name == null || name.trim().isEmpty()){
            errors.rejectValue("name", "이름(닉네임)은 필수입니다");
        } else if(userService.isExistName(name)){
            errors.rejectValue("name", "이미 존재하는 이름(닉네임)입니다");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "password 는 필수입니다");


        // 입력 password, re_password 가 동일한지 비교
        if(!user.getPassword().equals(user.getRe_password())){
            errors.rejectValue("re_password", "비밀번호와 비밀번호 확인 입력값이 다릅니다");
        }

        //주민등록번호 검증
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "juminNo", "주민등록번호 입력은 필수입니다");

    }
}
