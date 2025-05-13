package com.lec.spring.controller;

import com.lec.spring.domain.Post;
import com.lec.spring.service.BoardService;
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

@Controller
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    public BoardController(BoardService boardService) {
        System.out.println("일단 생성");
        this.boardService = boardService;
    }
// 수정, 추가의 경우 attr name을 result 로 하였음
    @GetMapping("/write")
    public void write (){}

    @PostMapping("/write")
    public String write (@Valid Post post,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("title", post.getTitle());
            redirectAttributes.addFlashAttribute("content", post.getContent());
            for (FieldError error : bindingResult.getFieldErrors()) {
                redirectAttributes.addFlashAttribute("error_" +  error.getField(),error.getDefaultMessage());
            }
            return "redirect:/board/write";
        }
        int result = boardService.write(post);
        model.addAttribute("result", result);
        return "board/write";
    }
    @GetMapping("/list")
    public String list (Model model){
        model.addAttribute("board", boardService.list());
        return "board/list";
    }
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model){
        model.addAttribute("board", boardService.detail(id));
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
        return "board/update" + post.getId();
    }
    @GetMapping("/delete")
    public String delete(Long id, Model model){
        System.out.println("삭제결과");
        model.addAttribute("result", boardService.detail(id));
        return "board/delete";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder){
        System.out.println("호출 성공");
        binder.setValidator(new BoardValidator());
    }

}
