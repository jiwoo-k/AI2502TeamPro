// src/main/java/com/lec/spring/controller/MyPageController.java
package com.lec.spring.controller;

import com.lec.spring.config.PrincipalDetails;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.Comment;
import com.lec.spring.domain.User;
import com.lec.spring.domain.ProfileUpdateForm;
import com.lec.spring.service.MypageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MyPageController {

    private final MypageService mypageService;

    public MyPageController(MypageService mypageService) {
        this.mypageService = mypageService;
    }

    /** 1) 마이페이지 메인 **/
    @GetMapping("/mypage/myPageMain")
    public String myPageMain(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        Long userId = principal.getUser().getId();

        User user = mypageService.getUserById(userId);
        model.addAttribute("user", user);
        return "mypage/myPageMain";
    }

    /** 2) 내가 쓴 글 (페이징 + type 필터링) **/
    @GetMapping("/mypage/myPosts")
    public String myPosts(
            @RequestParam(value = "selectedType", required = false) String selectedType,
            Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = ((PrincipalDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUser().getId();

        Page<Post> postsPage = mypageService.getMyPosts(userId, selectedType, pageable);
        model.addAttribute("posts", postsPage);
        model.addAttribute("selectedType", selectedType);
        return "mypage/myPosts";
    }

    /** 3) 내가 쓴 댓글 (페이징) **/
    @GetMapping("/mypage/myComments")
    public String myComments(
            Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = ((PrincipalDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUser().getId();

        Page<Comment> commentsPage = mypageService.getMyComments(userId, pageable);
        model.addAttribute("comments", commentsPage);
        return "mypage/myComments";
    }

    /** 4) 내가 팔로잉한 사용자 목록 **/
    @GetMapping("/mypage/myFollowing")
    public String myFollowing(Model model) {
        Long userId = ((PrincipalDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUser().getId();

        List<User> following = mypageService.getMyFollowing(userId);
        model.addAttribute("following", following);
        return "mypage/myFollowing";
    }

    /** 5) 프로필 수정 폼 **/
    @GetMapping("/mypage/editProfile")
    public String editProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principal = (PrincipalDetails) auth.getPrincipal();
        Long userId = principal.getUser().getId();

        ProfileUpdateForm form = mypageService.getProfileUpdateForm(userId);
        model.addAttribute("form", form);
        return "mypage/editProfile";
    }

    /** 6) 프로필 업데이트 **/
    @PostMapping("/mypage/updateProfile")
    public String updateProfile(
            @Validated @ModelAttribute("form") ProfileUpdateForm form,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "mypage/editProfile";
        }
        Long userId = ((PrincipalDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUser().getId();

        User user = new User();
        user.setId(userId);
        user.setName(form.getName());
        user.setPassword(form.getNewPassword());
        user.setTags(Arrays.stream(form.getTags().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList()));

        mypageService.updateUserProfile(user);
        return "redirect:/mypage/myPageMain";
    }
}
