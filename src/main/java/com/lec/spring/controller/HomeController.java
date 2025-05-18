package com.lec.spring.controller;

import com.lec.spring.domain.LocationInfo;
import com.lec.spring.domain.User;
import com.lec.spring.service.UserService;
import com.lec.spring.util.U;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/")
    public String home(Model model) {
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public void home() {

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
