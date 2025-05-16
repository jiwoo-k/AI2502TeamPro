package com.lec.spring.controller;

import com.lec.spring.domain.Post;
import com.lec.spring.domain.Tag;
import com.lec.spring.service.BoardService;
import com.lec.spring.service.UserFollowingService;
import com.lec.spring.vaildator.BoardValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final UserFollowingService userFollowingService;

    public BoardController(BoardService boardService, UserFollowingService userFollowingService) {
        System.out.println("일단 생성");
        this.boardService = boardService;
        this.userFollowingService = userFollowingService;
    }

    // 수정, 추가. 삭제의 경우 attr name을 result 로 하였음
    @GetMapping("/write")
    public void write() {
    }


    @PostMapping("/write")
    public String write(@Valid Post post,
                        BindingResult bindingResult,
                        Model model,
                        RedirectAttributes redirectAttributes
    ) {
        // vaildator
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("title", post.getTitle());
            redirectAttributes.addFlashAttribute("content", post.getContent());
            for (FieldError error : bindingResult.getFieldErrors()) {
                redirectAttributes.addFlashAttribute("error_" + error.getField(), error.getDefaultMessage());
            }
            return "redirect:/board/write";
        }
        int result = boardService.write(post);
        model.addAttribute("result", result);
        return "board/writeOk";
    }

    // listBytype (손님, 도우미 선택 가능 => findAll 은 혹시 몰라서 일부러 놔뒀음)
    @GetMapping("/list")
    public String list(@RequestParam(required = false) String type, Model model, @RequestParam(required = false) Boolean follow) {
        // 손님, 도우미 타입 설정
        if (type == null || type.isBlank()) {
            type = "guest";
        }
        List<Post> posts = boardService.listByType(type);
        boolean followFlag = (follow != null) ? follow : false;
        // 팔로우 여부
        Long loginUserId = 1L; // 고정 id
        for (Post post : posts) {
            boolean isFollowed = userFollowingService.isFollowing(loginUserId, post.getUser_id());
            post.setFollow(isFollowed);
        }


        model.addAttribute("follow", followFlag);
        model.addAttribute("board", posts);
        model.addAttribute("selectedType", type);
        System.out.println("과연!" + posts + followFlag + type);

        return "board/list";
    }


    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id,
                         Model model,
                         @RequestParam(required = false) String type,
                         @RequestParam(required = false) Boolean follow) {

        if (type == null || type.isBlank()) {
            type = "guest";
        }

        Long loginUserId = 1L;

        List<Post> posts = boardService.listByType(type);
        for (Post p : posts) {
            boolean isFollowed = userFollowingService.isFollowing(loginUserId, p.getUser_id());
            p.setFollow(isFollowed);
        }
        model.addAttribute("postList", posts);

        Post post = boardService.detail(id);
        boolean isFollowed = userFollowingService.isFollowing(loginUserId, post.getUser_id());
        post.setFollow(isFollowed);
        model.addAttribute("board", post);

        model.addAttribute("follow", (follow != null) ? follow : false);
        model.addAttribute("selectedType", type);

        return "board/detail";
    }

    @GetMapping("/update/{id}")
    public String update(Model model, @PathVariable Long id) {
        model.addAttribute("board", boardService.detail(id));
        return "board/update";
    }

    @PostMapping("/update")
    public String update(@Valid Post post,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("title", post.getTitle());
            redirectAttributes.addFlashAttribute("content", post.getContent());
            for (FieldError error : bindingResult.getFieldErrors()) {
                redirectAttributes.addFlashAttribute("error_" + error.getField(), error.getDefaultMessage());
            }
            return "redirect:/board/update/" + post.getId();
        }
        int result = boardService.update(post);
        model.addAttribute("result", result);
        return "board/updateOk";
    }

    @PostMapping("/delete")
    public String delete(Long id, Model model) {
        System.out.println("삭제결과" + boardService.delete(id));
        model.addAttribute("result", boardService.delete(id));
        return "board/deleteOk";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        System.out.println("호출 성공");
        binder.setValidator(new BoardValidator());
    }

}
