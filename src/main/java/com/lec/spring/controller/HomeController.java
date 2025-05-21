package com.lec.spring.controller;

import com.lec.spring.domain.*;
import com.lec.spring.repository.TagRepository;
import com.lec.spring.service.CategoryService;
import com.lec.spring.service.UserService;
import com.lec.spring.util.U;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService, TagRepository tagRepository, CategoryService categoryService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String home(Model model){
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public void home(){

    }

    @PostMapping("/location")
    @ResponseBody
    public User location(LocationInfo locationInfo){
        System.out.println(locationInfo);

        User user = U.getLoggedUser();

        if(user == null){
            U.getSession().setAttribute("lat", locationInfo.getLat());
            U.getSession().setAttribute("lng", locationInfo.getLng());
            U.getSession().setAttribute("areaName", locationInfo.getAreaName());
        }
        else {
            user.setLatitude(locationInfo.getLat());
            user.setLongitude(locationInfo.getLng());
            user.setAreaName(locationInfo.getAreaName());

            userService.updateLocation(user);
        }

        System.out.println(user);
        return user;
    }
}
