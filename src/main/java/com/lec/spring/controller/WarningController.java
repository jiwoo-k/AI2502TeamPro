package com.lec.spring.controller;

import com.lec.spring.domain.Post;
import com.lec.spring.domain.User;
import com.lec.spring.domain.UserWarning;
import com.lec.spring.service.UserWarningService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
public class WarningController {
    private final UserWarningService userWarningService;

    public WarningController (UserWarningService userWarningService) {
        this.userWarningService = userWarningService;
    }


    @PostMapping("/warning")
    public String warning(UserWarning warning,
                          Model model,
                          @AuthenticationPrincipal(expression = "user") User loginUser

    ) {
        warning.setComplaintUserId(loginUser.getId());
        Post post = warning.getPost();
        Long postId = warning.getPostId();


        model.addAttribute("result", warning);
        model.addAttribute("id", loginUser.getId());
        model.addAttribute("postId", postId);
        System.out.println("warning " + warning);
        userWarningService.report(warning);
        return "board/warning";
    }

}
