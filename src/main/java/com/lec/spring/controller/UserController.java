package com.lec.spring.controller;

import com.lec.spring.domain.User;
import com.lec.spring.domain.UserValidator;
import com.lec.spring.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public void login(Model model) {

    }

    @PostMapping("/loginError")
    public String loginError(HttpServletRequest request, Model model) {
        String errorMessage = (String) request.getAttribute("errorMessage");

        // 디버깅: 여기서 errorMessage 값이 제대로 넘어왔는지 다시 한번 확인
        System.out.println("### /user/loginError Handler: errorMessage from Request Attribute = " + errorMessage);
        return "user/login";
    }

    @GetMapping("/register")
    public void register(){

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
//            redirectAttributes.addFlashAttribute("juminNo", user.getJuminNo());


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

    @Autowired
    UserValidator userValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder){
        binder.setValidator(userValidator);
    }
}
