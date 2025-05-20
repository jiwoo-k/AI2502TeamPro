package com.lec.spring.controller;

import com.lec.spring.domain.Post;
import com.lec.spring.domain.Tag;
import com.lec.spring.domain.User;
import com.lec.spring.domain.UserWarning;
import com.lec.spring.service.BoardService;
import com.lec.spring.service.UserFollowingService;
import com.lec.spring.service.UserService;
import com.lec.spring.service.UserWarningService;
import com.lec.spring.vaildator.BoardValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final UserFollowingService userFollowingService;
    private final UserWarningService userWarningService;
    private final UserService userService;
    public BoardController(BoardService boardService, UserFollowingService userFollowingService, UserWarningService userWarningService, UserService userService) {
        System.out.println("일단 생성");
        this.boardService = boardService;
        this.userFollowingService = userFollowingService;
        this.userWarningService = userWarningService;
        this.userService = userService;
    }
// 수정, 추가. 삭제의 경우 attr name을 result 로 하였음
    @GetMapping("/write")
    public void write (){}


    @PostMapping("/write")
    public String write (@Valid Post post,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal(expression = "user") User loginUser
    ) {

        // vaildator
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("title", post.getTitle());
            redirectAttributes.addFlashAttribute("content", post.getContent());
            for (FieldError error : bindingResult.getFieldErrors()) {
                redirectAttributes.addFlashAttribute("error_" +  error.getField(),error.getDefaultMessage());
            }
            return "redirect:/board/write";
        }

        // user_id 가지고 오기
        post.setUser_id(loginUser.getId());
        int result = boardService.write(post);
        model.addAttribute("result", result);
        return "board/write";
    }
    // listBytype (손님, 도우미 선택 가능 => findAll 은 혹시 몰라서 일부러 놔뒀음)
    @GetMapping("/list")
    public String list(@RequestParam(required = false) String type,
                       Model model,
                       @RequestParam(required = false) Boolean follow,
                       Principal principal) {

        if (type == null || type.isBlank()) {
            type = "guest";
        }

        List<Post> posts = boardService.listByType(type);

        // 로그인 사용자 ID 추출
        Long loginUserId = null;
        if (principal != null) {
            String username = principal.getName();
            User loginUser = userService.findByUsername(username);
            loginUserId = loginUser.getId();
        }

        for (Post post : posts) {
            boolean isFollowed = loginUserId != null && userFollowingService.isFollowing(loginUserId, post.getUser_id());
            post.setFollow(isFollowed);
        }

        model.addAttribute("follow", (follow != null) ? follow : false);
        model.addAttribute("board", posts);
        model.addAttribute("selectedType", type);
        model.addAttribute("posts", posts);


        return "board/list";
    }



    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id,
                         Model model,
                         @RequestParam(required = false, name = "type") String type,
                         @RequestParam(required = false) Boolean follow,
                         Principal principal
    ) {

        if (type == null || type.isBlank()) {
            type = "guest";
        }

        Long loginUserId = null;
        if (principal != null) {
            String username = principal.getName();
            User loginUser = userService.findByUsername(username);
            loginUserId = loginUser.getId();
        }

        List<Post> posts = boardService.listByType(type);
        for (Post p : posts) {
            boolean isFollowed = loginUserId != null && userFollowingService.isFollowing(loginUserId, p.getUser_id());
            p.setFollow(isFollowed);
        }
        model.addAttribute("postList", posts);

        Post post = boardService.detail(id);
        boolean isFollowed = loginUserId != null && userFollowingService.isFollowing(loginUserId, post.getUser_id());
        post.setFollow(isFollowed);
        model.addAttribute("board", post);

        //  신고 횟수
        int warningCount = userWarningService.postWarningCount(id); // id는 게시물 id
        model.addAttribute("postWarningCount", warningCount);

        model.addAttribute("follow", (follow != null) ? follow : false);
        model.addAttribute("selectedType", type);

        // User 객체 가져오기
        User postUser = userService.findByName(post.getName());
        model.addAttribute("user", postUser);

        // 이미 신고한 게시물이면 더이상 신고하지 못하게 하기
        final Long userId = loginUserId;
        List <UserWarning> warnings = userWarningService.getWarningsByPostId(id);
        boolean hasReported = warnings.stream()
                .anyMatch(w -> w.getComplaintUserId().equals(userId));
        model.addAttribute("hasReported", hasReported);

        return "board/detail";
    }



    @GetMapping("/update/{id}")
    public String update(Model model, @PathVariable Long id){
        model.addAttribute("board", boardService.detail(id));
        return "board/update";
    }
    @PostMapping("/update")
    public String update(@Valid Post post,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes,
                         @AuthenticationPrincipal(expression = "user") User loginUser
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("title", post.getTitle());
            redirectAttributes.addFlashAttribute("content", post.getContent());
            for (FieldError error : bindingResult.getFieldErrors()) {
                redirectAttributes.addFlashAttribute("error_" + error.getField(), error.getDefaultMessage());
            }
            return "redirect:/board/update/" + post.getId();
        }
        post.setUser_id(loginUser.getId());
        int result = boardService.update(post);
        model.addAttribute("result", result);


        return "board/updateOk" ;
    }
    @PostMapping("/delete")
    public String delete(Long id, Model model, @AuthenticationPrincipal(expression = "user") User loginUser) {
        boardService.deleteTime(id);
        model.addAttribute("result", 1);
        return "board/deleteOk";
    }


    @PostMapping("/warning")
    public String warning(UserWarning warning, Model model, @AuthenticationPrincipal(expression = "user") User loginUser
    ) {
        warning.setComplaintUserId(loginUser.getId());

        model.addAttribute("result", warning);
        System.out.println("warning " + warning);
        userWarningService.report(warning);
        return "board/warning";
    }


    @InitBinder("post")
    public void initBinder(WebDataBinder binder){
        System.out.println("호출 성공");
        binder.setValidator(new BoardValidator());
    }

}
