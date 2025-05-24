package com.lec.spring.controller;

import com.lec.spring.domain.User;
import com.lec.spring.repository.UserWarningRepository;
import com.lec.spring.service.UserService;
import com.lec.spring.service.UserWarningService;
import com.lec.spring.service.UserWarningServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserWarningService userWarningService;
    private final UserService userService;

    public AdminController(UserWarningService userWarningService, UserService userService) {
        this.userWarningService = userWarningService;
        this.userService = userService;
    }

    @RequestMapping("/main")
    public String mainPage() {
        return "admin/main";
    }

    @GetMapping("/users")
    public String usersPage(@RequestParam(required = false, defaultValue = "5") Integer warningCount, Model model) {
        int cnt = (warningCount == null) ? 0 : warningCount;

        List<User> users = usersByWarnCount(cnt);
        model.addAttribute("userList", users);
        model.addAttribute("cnt", cnt);
        return "admin/users";
    }


    @PostMapping("/account/limit/{userId}")
    public String limitAccount(@PathVariable Long userId, Integer days, RedirectAttributes redirectAttributes) {
        userService.limitUser(userId, days);
        Integer cnt;

        if(days == null) {
            redirectAttributes.addFlashAttribute("banSuccess", "영구 정지 처리가 완료되었습니다.");
            cnt = 15;
        }
        else if(days == 3) {
            redirectAttributes.addFlashAttribute("pauseSuccess", "3일 정지 처리가 완료되었습니다.");
            cnt = 5;
        }
        else {
            redirectAttributes.addFlashAttribute("pauseSuccess", "7일 정지 처리가 완료되었습니다.");
            cnt = 10;
        }

        return "redirect:/admin/users?warningCount="+cnt;
    }

    private List<User> usersByWarnCount(Integer warningCount){
        List<User> users;

        switch (warningCount){
            case 0:
                users = userService.findUsersByWarnCount(0, 4);
                break;
            case 5:
                users = userService.findUsersByWarnCount(5, 9);
                break;
            case 10:
                users = userService.findUsersByWarnCount(10, 14);
                break;
            case 15:
                users = userService.findUsersByWarnCount(15, Integer.MAX_VALUE); //어차피 15회 이상은 탈퇴이기 때문에 그 이상일 수가 없다..
                break;
            default:
                users = userService.findUsersByWarnCount(0, 4);
        }

        return users;
    }


    @PostMapping("/follower/reset")
    public String followerReset(){

        return "redirect:/admin/users";
    }
}
