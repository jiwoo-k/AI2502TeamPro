// src/main/java/com/lec/spring/controller/MyPageController.java
package com.lec.spring.controller;

import com.lec.spring.config.PrincipalDetails;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.Comment;
import com.lec.spring.domain.User;
import com.lec.spring.domain.ProfileUpdateForm;
import com.lec.spring.service.MyPageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MyPageController {

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    /** 1) 마이페이지 메인 **/
    @GetMapping("/mypage")
    public String myPageMain(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        Long userId = principal.getUser().getId();

        User user = myPageService.getUserById(userId);
        model.addAttribute("user", user);
        return "mypage/myPageMain";
    }

    /** 2) 내가 쓴 글 (페이징 + type 필터링) **/
    @GetMapping("/mypage/post")
    public String myPosts(
            @RequestParam(value = "selectedType", required = false) String selectedType,
            Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = ((PrincipalDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUser().getId();

        Page<Post> postsPage = myPageService.getMyPosts(userId, selectedType, pageable);
        model.addAttribute("posts", postsPage);
        model.addAttribute("selectedType", selectedType);
        return "mypage/myPosts";
    }

    /** 3) 내가 쓴 댓글 (페이징) **/
    @GetMapping("/mypage/comment")
    public String myComments(
            Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = ((PrincipalDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUser().getId();

        Page<Comment> commentsPage = myPageService.getMyComments(userId, pageable);
        model.addAttribute("comments", commentsPage);
        return "mypage/myComments";
    }

    /** 4) 내가 팔로잉한 사용자 목록 (페이징) **/
    @GetMapping("/mypage/follow")
    public String myFollowing(
            Model model,
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Long userId = ((PrincipalDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUser().getId();

        Page<User> page = myPageService.getMyFollowing(userId, pageable);
        model.addAttribute("following", page);
        return "mypage/myFollowing";
    }

    @GetMapping("/mypage/follow/{userId}")
    public String viewUserPosts(
            @PathVariable Long userId,
            @RequestParam(value = "selectedType", required = false) String selectedType,
            Model model,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        // (1) 내 아이디
        Long principalId = ((PrincipalDetails)
                SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal())
                .getUser().getId();

        // (2) 클릭한 사용자의 게시글 목록
        Page<Post> postsPage = myPageService.getMyPosts(userId, selectedType, pageable);

        // (3) 클릭한 사용자 정보 조회
        User viewUser = myPageService.getUserById(userId);

        // (4) 모델에 담기
        model.addAttribute("posts", postsPage);
        model.addAttribute("selectedType", selectedType);
        model.addAttribute("viewUser", viewUser);
        model.addAttribute("principalId", principalId);

        return "mypage/myPosts";
    }



    // 팔로우
    @PostMapping("/mypage/follow")
    @ResponseStatus(HttpStatus.OK)
    public void follow(
            @RequestParam Long userId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long me = principal.getUser().getId();
        myPageService.followUser(me, userId);
    }

    // 언팔로우
    @PostMapping("/mypage/unfollow")
    @ResponseStatus(HttpStatus.OK)
    public void unfollow(
            @RequestParam Long userId,
            @AuthenticationPrincipal PrincipalDetails principal
    ) {
        Long me = principal.getUser().getId();
        myPageService.unfollowUser(me, userId);
    }




    /** 5) 프로필 수정 폼 **/
    @GetMapping("/mypage/edit")
    public String editProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principal = (PrincipalDetails) auth.getPrincipal();
        Long userId = principal.getUser().getId();

        ProfileUpdateForm form = myPageService.getProfileUpdateForm(userId);
        model.addAttribute("profileUpdateForm", form);
        return "mypage/editProfile";
    }

    /** 6) 프로필 업데이트 **/
    @PostMapping("/mypage/update")
    public String updateProfile(
            @Validated @ModelAttribute("profileUpdateForm") ProfileUpdateForm form,
            BindingResult bindingResult
    ) {
        // 입력 검증 에러가 있으면 다시 수정 페이지로
        if (bindingResult.hasErrors()) {
            return "mypage/editProfile";
        }

        // 현재 로그인한 회원의 ID 조회
        Long userId = ((PrincipalDetails)
                SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal())
                .getUser().getId();

        // 업데이트할 User 객체 생성
        User user = new User();
        user.setId(userId);
        user.setName(form.getName());

        // 새 비밀번호가 입력된 경우에만 설정
        if (form.getNewPassword() != null && !form.getNewPassword().isBlank()) {
            user.setPassword(form.getNewPassword());
        }

        // 태그 리스트 설정
        user.setTags(Arrays.stream(form.getTags().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList()));

        // 서비스 호출 (트랜잭션이 걸린 updateUserProfile 메서드에서 실제 DB 반영)
        myPageService.updateUserProfile(user);

        // 수정 후 마이페이지로 리다이렉트
        return "redirect:/mypage";
    }
}
