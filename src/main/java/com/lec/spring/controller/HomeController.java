package com.lec.spring.controller;

import com.lec.spring.domain.*;
import com.lec.spring.repository.TagRepository;
import com.lec.spring.service.CategoryService;
import com.lec.spring.service.UserService;
import com.lec.spring.util.U;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    private final UserService userService;
    private final TagRepository tagRepository;
    private final CategoryService categoryService;

    public HomeController(UserService userService, TagRepository tagRepository, CategoryService categoryService) {
        this.userService = userService;
        this.tagRepository = tagRepository;
        this.categoryService = categoryService;
    }

    @RequestMapping("/")
    public String home(Model model){
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public void home(){

    }

    @PostMapping("/location")
    @ResponseBody
    public User location(LocationInfo locationInfo){
        System.out.println(locationInfo);

        User user = U.getLoggedUser();

        if(user == null){
            U.getSession().setAttribute("lat", locationInfo.getLat());
            U.getSession().setAttribute("lng", locationInfo.getLng());
            U.getSession().setAttribute("areaName", locationInfo.getAreaName());
        }
        else {
            user.setLatitude(locationInfo.getLat());
            user.setLongitude(locationInfo.getLng());
            user.setAreaName(locationInfo.getAreaName());

            userService.updateLocation(user);
        }

        System.out.println(user);
        return user;
    }

    @GetMapping("/tag")
    public String showTag(Model model){
        List<Category> categoryList = categoryService.list();
        model.addAttribute("categoryList", categoryList);

        return "common/tag";
    }

    @PostMapping("/tag")
    public String searchTag(TagSearch tagSearch,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes){

        //검색 input 빈값인가?
        if(tagSearch.getTagName() == null || tagSearch.getTagName().trim().isEmpty()){
            result.rejectValue("tagName", "검색어 입력은 필수입니다.");
        }

        //카테고리 선택 했는가?
        if(tagSearch.getCategoryId() == null){
            result.rejectValue("categoryId", "카테고리 선택은 필수입니다.");
        }


        if(result.hasErrors()){
            for(FieldError err : result.getFieldErrors()){
                redirectAttributes.addFlashAttribute("error_" + err.getField(), err.getCode());

            redirectAttributes.addFlashAttribute("name", tagSearch.getTagName());

            List<Category> categoryList = categoryService.list(); // 카테고리 목록 다시 가져오기
            redirectAttributes.addFlashAttribute("categoryList", categoryList);

            redirectAttributes.addFlashAttribute("submittedCategoryId", tagSearch.getCategoryId());
            }

            return "redirect:/tag";
        }

        Tag searchedTag = tagRepository.searchTag(tagSearch.getCategoryId(), tagSearch.getTagName().trim());
        List<Category> categoryList = categoryService.list();
        model.addAttribute("categoryList", categoryList);


        model.addAttribute("searchedTag", searchedTag);

        String searchedTagCategoryName = categoryService.findById(searchedTag.getCategory_id()).getName();
        model.addAttribute("searchedTagCategoryName", searchedTagCategoryName);


        return "common/tag";
    }
}
