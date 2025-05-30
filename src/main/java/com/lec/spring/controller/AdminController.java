package com.lec.spring.controller;

import com.lec.spring.domain.*;
import com.lec.spring.repository.UserFollowingRepository;
import com.lec.spring.repository.UserWarningRepository;
import com.lec.spring.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserWarningService userWarningService;
    private final UserService userService;
    private final UserFollowingService userFollowingService;
    private final CategoryService categoryService;

    public AdminController(UserWarningService userWarningService, UserService userService, UserFollowingService userFollowingService, CategoryService categoryService) {
        this.userWarningService = userWarningService;
        this.userService = userService;
        this.userFollowingService = userFollowingService;
        this.categoryService = categoryService;
    }

    @RequestMapping("/main")
    public String mainPage(Model model, LocalDate startDate, LocalDate endDate, RedirectAttributes redirectAttributes) {
        List<Category> categories = categoryService.list();
        List<LoginHistory> loginHistories = userService.findLoginHistory(startDate, endDate);

        if(startDate != null && endDate != null && startDate.isAfter(endDate)) {
            redirectAttributes.addFlashAttribute("dateError", "시작 날짜는 종료 날짜보다 이후일 수 없습니다.");

            return "redirect:/admin/main";
        }

        model.addAttribute("categories", categories);
        model.addAttribute("loginHistories", loginHistories);

        return "admin/main";
    }

    @GetMapping("/users")
    public String usersPage(@RequestParam(required = false, defaultValue = "5") Integer warningCount, Model model) {
        int cnt = (warningCount == null) ? 0 : warningCount;

        List<User> users = usersByWarnCount(cnt);

        if(cnt == 10){
            for (User user : users) {
                user.setFollowersCount(userFollowingService.followCount(user.getId()));
            }
        }

        model.addAttribute("userList", users);
        model.addAttribute("cnt", cnt);
        return "admin/users";
    }


    @PostMapping("/account/limit/{userId}")
    public String limitAccount(@PathVariable Long userId, Integer days, RedirectAttributes redirectAttributes) {
        userService.limitUser(userId, days);
        int cnt;

        if(days == null) {
//            redirectAttributes.addFlashAttribute("banSuccess", "영구 정지 처리가 완료되었습니다.");
            cnt = 15;
        }
        else if(days == 3) {
//            redirectAttributes.addFlashAttribute("pauseSuccess", "3일 정지 처리가 완료되었습니다.");
            cnt = 5;
        }
        else {
//            redirectAttributes.addFlashAttribute("pauseSuccess", "7일 정지 처리가 완료되었습니다.");
            cnt = 10;
        }

        return "redirect:/admin/users?warningCount="+cnt;
    }

    @PostMapping("/follower/reset/{userId}")
    public String followerReset(@PathVariable Long userId, RedirectAttributes redirectAttributes){
        //이 두 줄은 굳이 필요한 작업일까 싶긴하지만..
        User user = userService.findByUserId(userId);
        user.setFollowersCount(0);

        //이 사용자의 팔로우 관계를 받아오기
        List<UserFollowing> followerList = userFollowingService.getFollowersList(userId);

        for(UserFollowing follow : followerList){
            userFollowingService.unfollow(follow.getFollowingUserId(), follow.getFollowedUserId());
        }

        return "redirect:/admin/users?warningCount=10";
    }

    @RequestMapping("/users/detail/{userId}")
    public String userDetailsPage(@PathVariable Long userId, Model model) {
        List<UserWarning> warnDetails = userWarningService.findWarningDetails(userId);

        model.addAttribute("warnDetailList", warnDetails);

        return "admin/userDetail";
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
                users = userService.findUsersByWarnCount(0, Integer.MAX_VALUE);
        }

        return users;
    }
}
