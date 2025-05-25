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

    // 1) 목록 페이지
    @GetMapping("/list")
    public String list(Model model) {
        List<Category> categories = categoryService.list();
        model.addAttribute("categories", categories);
        return "admin/category";
    }

    // 2) 삭제 처리
    @PostMapping("/delete")
    public String delete(@RequestParam("selectedId") Long id) {
        categoryService.delete(id);
        return "redirect:/admin/category/list";
    }

    // 3) 추가·수정 폼 이동
    @GetMapping({"/add", "/edit/{id}"})
    public String form(@PathVariable(required = false) Long id, Model model) {
        Category category = (id == null)
                ? new Category()
                : categoryService.findById(id);
        model.addAttribute("category", category);
        return "admin/category/form";
    }

    // 4) 저장 처리
    @PostMapping("/save")
    public String save(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/admin/category";
    }
}
