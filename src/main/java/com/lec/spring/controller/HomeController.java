package com.lec.spring.controller;

import com.lec.spring.domain.*;
import com.lec.spring.repository.TagRepository;
import com.lec.spring.service.CategoryService;
import com.lec.spring.service.UserService;
import com.lec.spring.util.U;
import com.lec.spring.vaildator.TagValidator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final TagRepository tagRepository;

    List<Tag> selectedTags = null;

    public HomeController(UserService userService, TagRepository tagRepository, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.tagRepository = tagRepository;
    }

    @RequestMapping("/")
    public String home(Model model){
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public void home(Model model, HttpSession httpSession){
//        httpSession.removeAttribute("selectedTags");
        List<Category> categoryList = categoryService.list();
        model.addAttribute("categoryList", categoryList);

        selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");
        String areaName = (String) httpSession.getAttribute("areaName");
        if(selectedTags == null){
            //새로 만들기
            selectedTags = new ArrayList<>();
            httpSession.setAttribute("selectedTags", selectedTags);
        }

        model.addAttribute("selectedTags", selectedTags);
        model.addAttribute("areaName", areaName);
    }

    @PostMapping("/home")
    public String searchTag(@Valid Tag tag,
                            BindingResult result,
                            @RequestHeader(value = HttpHeaders.REFERER, required = false) String referer,
                            Model model,
                            RedirectAttributes redirectAttributes,
                            HttpSession httpSession)
    {
        String path = "/";

        if(referer != null && !referer.isEmpty()){
            try {
                URI uri = new URI(referer);
                path = uri.getPath();
            } catch (URISyntaxException e) {
                // URL 형식이 잘못된 경우
                System.err.println("Referer URL 파싱 오류: " + referer);
            }
        }

        if(result.hasErrors()){
            for(FieldError err : result.getFieldErrors()){
                redirectAttributes.addFlashAttribute("error_" + err.getField(), err.getCode());

                redirectAttributes.addFlashAttribute("name", tag.getName());

                List<Category> categoryList = categoryService.list(); // 카테고리 목록 다시 가져오기
                redirectAttributes.addFlashAttribute("categoryList", categoryList);

                redirectAttributes.addFlashAttribute("submittedCategoryId", tag.getCategory_id());

            /*String tagColor = categoryService.findById(tag.getCategory_id()).getColor();
            redirectAttributes.addFlashAttribute("tagColor", tagColor);*/
            }

            return "redirect:" + path;
        }

        //유효성 검사 경우의 수에 따라 이게 null 이 될 일은 없긴함 ..
        Tag searchedTag = tagRepository.searchTag(tag);

        // 태그 색깔 가져와야한다.
        String color = categoryService.findById(searchedTag.getCategory_id()).getColor();
        searchedTag.setColor(color);

        selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");
        String areaName = (String) httpSession.getAttribute("areaName");

        model.addAttribute("selectedTags", selectedTags);

        List<Category> categoryList = categoryService.list();
        model.addAttribute("categoryList", categoryList);

        model.addAttribute("searchedTag", searchedTag);
        model.addAttribute("name", searchedTag.getName());
        model.addAttribute("submittedCategoryId", searchedTag.getCategory_id());

        String searchedTagCategoryName = categoryService.findById(searchedTag.getCategory_id()).getName();
        model.addAttribute("searchedTagCategoryName", searchedTagCategoryName);
        model.addAttribute("areaName", areaName);


        return path.substring(1);
    }

    @PostMapping("/location")
    @ResponseBody
    public LocationInfo location(LocationInfo locationInfo){
        System.out.println(locationInfo);

        User user = U.getLoggedUser();

        if(user != null){
            user.setLatitude(locationInfo.getLat());
            user.setLongitude(locationInfo.getLng());
            user.setAreaName(locationInfo.getAreaName());

            userService.updateLocation(user);
        }

        U.getSession().setAttribute("lat", locationInfo.getLat());
        U.getSession().setAttribute("lng", locationInfo.getLng());
        U.getSession().setAttribute("areaName", locationInfo.getAreaName());

        System.out.println(user);
        return locationInfo;
    }

    @Autowired
    TagValidator tagValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder){
        binder.setValidator(tagValidator);
    }
}
