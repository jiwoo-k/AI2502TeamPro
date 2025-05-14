package com.lec.spring.controller;

import com.lec.spring.domain.User;
import com.lec.spring.service.UserService;
import jakarta.validation.Valid;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller("/user")
public class UserController {

    private final UserService userService;

    public UserController(SqlSession sqlSession) {
        this.userService = sqlSession.getMapper(UserService.class);
    }

    @GetMapping("/login")
    public void login(Model model) {

    }

    @GetMapping("/register")
    public void resgister(){

    }

    @PostMapping("/register")
    public String register(
            @Valid User user,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes)
    {
        //검증 에러가 있었다면 redirect 한다
        if(result.hasErrors()){
            redirectAttributes.addFlashAttribute("username", user.getUsername());
            redirectAttributes.addFlashAttribute("name", user.getName());


            List<FieldError> errList = result.getFieldErrors();
            for(FieldError err : errList) {
                redirectAttributes.addFlashAttribute("error", err.getCode());  // 가장 처음에 발견된 에러를 담아 보낸다
                break;
            }


            return "redirect:/user/register";
        }

        //에러 없었으면 회원 등록 진행
        String page = "user/registerOk";
        int cnt = userService.register(user);
        model.addAttribute("result", cnt);
        return page;
    }
}
