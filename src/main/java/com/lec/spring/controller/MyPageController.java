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
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class MyPageController {

    private static final Logger logger = LoggerFactory.getLogger(MyPageController.class);

    private final MyPageService myPageService;

    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    /**
     * 1) 마이페이지 메인
     **/
    @GetMapping("/mypage")
    public String myPageMain(@AuthenticationPrincipal PrincipalDetails principal, Model model) {
        Long userId = principal.getUser().getId();

        User user = myPageService.getUserById(userId);
        model.addAttribute("user", user);
        return "mypage/myPageMain";
    }

    /**
     * 2) 내가 쓴 글 (페이징 + type 필터링)
     **/
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

    /**
     * 3) 내가 쓴 댓글 (페이징)
     **/
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

    /**
     * 4) 내가 팔로잉한 사용자 목록 (페이징)
     **/
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


    /**
     * 5) 프로필 수정 폼
     **/
    @GetMapping("/mypage/edit")
    public String editProfile(Model model,
                              @AuthenticationPrincipal PrincipalDetails principal,
                              CsrfToken csrfToken) {
        model.addAttribute("_csrf", csrfToken);

        Long userId = principal.getUser().getId();
        ProfileUpdateForm form = myPageService.getProfileUpdateForm(userId);
        model.addAttribute("profileUpdateForm", form);
        return "mypage/editProfile";
    }

    /**
     * 6) 프로필 업데이트
     **/
    @PostMapping("/mypage/update")
    public String updateProfile(
            @Validated @ModelAttribute("profileUpdateForm") ProfileUpdateForm form,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal PrincipalDetails principal,
            CsrfToken csrfToken,
            RedirectAttributes redirectAttrs
    ) {
        // ① 기본 검증 에러 있으면 수정 페이지로
        if (bindingResult.hasErrors()) {
            model.addAttribute("_csrf", csrfToken);
            model.addAttribute("profileUpdateForm", form);
            return "mypage/editProfile";
        }

        // ② 새 비밀번호 입력 시에만 confirmPassword 검증
        if (form.getNewPassword() != null && !form.getNewPassword().isBlank()) {
            // 확인 비워졌으면 에러
            if (form.getConfirmPassword() == null || form.getConfirmPassword().isBlank()) {
                bindingResult.rejectValue(
                        "confirmPassword",
                        "empty",
                        "비밀번호 확인을 입력하세요."
                );
            }
            // 불일치 시 에러
            else if (!form.getNewPassword().equals(form.getConfirmPassword())) {
                bindingResult.rejectValue(
                        "confirmPassword",
                        "mismatch",
                        "비밀번호가 일치하지 않습니다."
                );
            }
            if (bindingResult.hasErrors()) {
                return "mypage/editProfile";
            }
        }

        // ③ 로그인 사용자 ID 가져오기
        Long userId = ((PrincipalDetails)
                SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal())
                .getUser().getId();

        // ④ User 엔티티에 설정
        User user = new User();
        user.setId(userId);
        user.setName(form.getName());
        if (form.getNewPassword() != null && !form.getNewPassword().isBlank()) {
            user.setPassword(form.getNewPassword());
        }
        user.setTags(Arrays.stream(form.getTags().split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList()));

        // ⑤ 로그, 서비스 호출, 플래시 메시지
        logger.info("▶▶ MyPageController.updateProfile 진입: {}", form);
        myPageService.updateUserProfile(user);
        redirectAttrs.addFlashAttribute("msg", "회원정보가 정상적으로 수정되었습니다.");
        return "redirect:/mypage";
    }

}
