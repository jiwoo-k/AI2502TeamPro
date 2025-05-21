package com.lec.spring.controller;

import com.lec.spring.domain.User;
import com.lec.spring.service.BoardService;
import com.lec.spring.service.UserFollowingService;
import com.lec.spring.service.UserService;
import com.lec.spring.service.UserWarningService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
@Controller
@RequestMapping("/board")
public class FollowController {
    private final UserFollowingService userFollowingService;
    private final UserService userService;
    public FollowController( UserFollowingService userFollowingService,  UserService userService) {
        System.out.println("일단 생성");
        this.userFollowingService = userFollowingService;
        this.userService = userService;
    }



    // 팔로우하기
    @PostMapping("/follow/insert")
    public String insertFollow(@RequestParam("followedUserId") Long followedUserId,
                               @AuthenticationPrincipal(expression = "user") User loginUser
            ,@RequestParam("followingUserId") Long followingUserId,
                               @RequestParam("id") Long postId
    ) {


        System.out.println("followingUserId: " + followedUserId);
        User followedUser = userService.findByUserId(followedUserId);
        System.out.println("followedUser: " + followedUser);

        if (followedUser == null) {
            System.out.println("팔로우하려는 사용자가 존재하지 않습니다.");
            return "redirect:/board/list";
        }
        userFollowingService.follow(loginUser, followedUser);
        return "redirect:/board/detail/" + postId;
    }


    // 언팔로우하기 ㅇㅇㅇㅇ
    @PostMapping("/follow/delete")
    public String deleteFollow(@RequestParam("followingUserId") Long followingUserId,
                               @AuthenticationPrincipal(expression = "user") User loginUser,
                               @RequestParam("followedUserId") Long followedUserId,
                               @RequestParam("id") Long postId
    ) {

        User followedUser = userService.findByUserId(followingUserId);

        if (followedUser == null) {
            System.out.println("팔로우하려는 사용자가 존재하지 않습니다.");
            return "redirect:/board/list";
        }
        userFollowingService.unfollow(loginUser, followedUser);
        return "redirect:/board/detail/" + postId;
    }

}
