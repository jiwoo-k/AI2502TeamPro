package com.lec.spring.controller;

import com.lec.spring.domain.Category;
import com.lec.spring.domain.Tag;
import com.lec.spring.domain.TagValidator;
import com.lec.spring.repository.TagRepository;
import com.lec.spring.service.CategoryService;
import com.lec.spring.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TagController {
    private final TagRepository tagRepository;
    private final CategoryService categoryService;

    public TagController(TagRepository tagRepository, CategoryService categoryService) {
        this.tagRepository = tagRepository;
        this.categoryService = categoryService;
    }

    List<Tag> selectedTags = null;

    @GetMapping("/tag")
    public String showTag(Model model, HttpSession httpSession){
        List<Category> categoryList = categoryService.list();
        model.addAttribute("categoryList", categoryList);

        // 세션에 저장시켜둔 태그 목록
        selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");
        if(selectedTags == null){
            //새로 만들기
            selectedTags = new ArrayList<>();
            httpSession.setAttribute("selectedTags", selectedTags);
        }

        //유효성 검사 실패로 get 으로 redirect 되어도 세션에서 갖고와서 뿌려줘야 목록이 유지됨
        model.addAttribute("selectedTags", selectedTags);

        return "common/tag";
    }

    @PostMapping("/tag")
    public String searchTag(@Valid Tag tag,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes,
                            HttpSession httpSession)
    {
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

            return "redirect:/tag";
        }

        //유효성 검사 경우의 수에 따라 이게 null 이 될 일은 없긴함 ..
        Tag searchedTag = tagRepository.searchTag(tag);

        // 태그 색깔 가져와야한다.
        String color = categoryService.findById(searchedTag.getCategory_id()).getColor();
        searchedTag.setColor(color);

        selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");

        model.addAttribute("selectedTags", selectedTags);

        List<Category> categoryList = categoryService.list();
        model.addAttribute("categoryList", categoryList);

        model.addAttribute("searchedTag", searchedTag);
        model.addAttribute("name", searchedTag.getName());
        model.addAttribute("submittedCategoryId", searchedTag.getCategory_id());

        String searchedTagCategoryName = categoryService.findById(searchedTag.getCategory_id()).getName();
        model.addAttribute("searchedTagCategoryName", searchedTagCategoryName);


        return "common/tag";
    }

    @PostMapping("/tag/add")
    @ResponseBody
    public Map<String, Object> tagAdd(Tag tag, HttpSession httpSession){
        System.out.println(tag);

        //response 정보를 담는 객체
        Map<String, Object> response = new HashMap<>();
        response.put("url", "/tag");

        selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");

        //신규 태그 추가 시 목록 개수 검증
        if(selectedTags.size() >= 5){
            response.put("sizeOverError", "태그는 최대 5개까지 담을 수 있습니다");
            return response;
        }


        Tag addTag = tagRepository.searchTag(tag);

        //기존에 있는 태그 추가시
        if(addTag != null){
            String tagColor = categoryService.findById(addTag.getCategory_id()).getColor();
            addTag.setColor(tagColor);

            //이미 담은 목록에 있다면..
            if(selectedTags.contains(addTag)){
                response.put("alreadyInList", "이미 목록에 추가된 태그입니다.");
                return response;
            }

            selectedTags.add(addTag);

            response.put("addExistingTag", addTag);
        }
        else{
            //기존에 없는 태그 추가시 db에 넣고 후처리
            int result = tagRepository.addTag(tag);
            tag = tagRepository.searchTag(tag);

            tag.setColor(categoryService.findById(tag.getCategory_id()).getColor());

            selectedTags.add(tag);

            response.put("result", result);
        }

        httpSession.setAttribute("selectedTags", selectedTags);

        System.out.println(response);
        return response;
    }

    @PostMapping("/tag/remove")
    @ResponseBody
    public Map<String, Object> tagRemove(Tag tag, HttpSession httpSession){
        Map<String, Object> response = new HashMap<>();
        response.put("url", "/tag");

        selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");

        //받아온 태그가 담은 목록에 있다면 찾아서 없애자
        boolean removed = selectedTags.removeIf(t ->
                t.getId().equals(tag.getId())
                        && t.getCategory_id().equals(tag.getCategory_id())
                        && t.getName().equals(tag.getName()));

        if (removed) {
            httpSession.setAttribute("selectedTags", selectedTags);
            response.put("deleteSuccess", "삭제 성공");
        }
        else{
            response.put("tagNotFound", "삭제하려는 태그를 찾을 수 없습니다");
        }

        return response;
    }

    @PostMapping("/tag/save")
    public void tagSave(List<Tag> tags, HttpSession httpSession){
        //TODO: 태그 저장 로직 작성
    }

    @Autowired
    TagValidator tagValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder){
        binder.setValidator(tagValidator);
    }
}
