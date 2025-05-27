package com.lec.spring.controller;

import com.lec.spring.domain.Category;
import com.lec.spring.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/category")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService;

    // 1) 카테고리 목록 페이지
    @GetMapping("/list")
    public String list(Model model) {
        List<Category> categories = categoryService.list();
        model.addAttribute("categoryList", categories);
        return "admin/category/list";
    }

    // 2) 카테고리 삭제 처리
    @PostMapping("/delete")
    public String delete(@RequestParam("selectedCategory") Long id) {
        categoryService.delete(id);
        return "redirect:/admin/category/list";
    }

    // 3) 카테고리 추가 폼
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category/form";
    }

    // 수정 폼 (GET 방식 + selectedCategory 파라미터)
    @GetMapping("/edit")
    public String editForm(@RequestParam("selectedCategory") Long id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        return "admin/category/form";
    }

    // 5) 저장 처리 (추가 + 수정)
    @PostMapping("/save")
    public String save(@ModelAttribute Category category) {
        // id가 null이면 새로 생성, 있으면 수정
        categoryService.save(category);
        return "redirect:/admin/category/list";
    }
}