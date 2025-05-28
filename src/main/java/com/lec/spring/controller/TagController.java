package com.lec.spring.controller;

import com.lec.spring.domain.Category;
import com.lec.spring.domain.Tag;
import com.lec.spring.service.TagService;
import com.lec.spring.vaildator.TagValidator;
import com.lec.spring.repository.TagRepository;
import com.lec.spring.service.BoardService;
import com.lec.spring.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@Controller
public class TagController {
    private final TagRepository tagRepository;
    private final CategoryService categoryService;

    public TagController(SqlSession sqlSession, CategoryService categoryService) {
        this.tagRepository = sqlSession.getMapper(TagRepository.class);
        this.categoryService = categoryService;
    }
    @GetMapping("/tag")
    public String showTag(Model model, HttpSession httpSession) {
        List<Category> categoryList = categoryService.list();
        model.addAttribute("categoryList", categoryList);

        List<Tag> selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");
        if (selectedTags == null) {
            selectedTags = new ArrayList<>();
            httpSession.setAttribute("selectedTags", selectedTags);
        }

        model.addAttribute("selectedTags", selectedTags);
        return "common/tag";
    }

    @PostMapping("/tag")
    public String searchTag(@Valid Tag tag,
                            BindingResult result,
                            @RequestHeader(value = HttpHeaders.REFERER, required = false) String referer,
                            Model model,
                            RedirectAttributes redirectAttributes,
                            HttpSession httpSession) {

        String path = "/";
        if (referer != null && !referer.isEmpty()) {
            try {
                URI uri = new URI(referer);
                path = uri.getPath();
            } catch (URISyntaxException e) {
                System.err.println("Referer URL 파싱 오류: " + referer);
            }
        }

        if (result.hasErrors()) {
            for (FieldError err : result.getFieldErrors()) {
                redirectAttributes.addFlashAttribute("error_" + err.getField(), err.getCode());
                redirectAttributes.addFlashAttribute("name", tag.getName());
                redirectAttributes.addFlashAttribute("submittedCategoryId", tag.getCategory_id());
                redirectAttributes.addFlashAttribute("categoryList", categoryService.list());
            }
            return "redirect:" + path;
        }

        Tag searchedTag = tagRepository.searchTag(tag);
        String color = categoryService.findById(searchedTag.getCategory_id()).getColor();
        searchedTag.setColor(color);

        List<Tag> selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");
        model.addAttribute("selectedTags", selectedTags);
        model.addAttribute("categoryList", categoryService.list());
        model.addAttribute("searchedTag", searchedTag);
        model.addAttribute("name", searchedTag.getName());
        model.addAttribute("submittedCategoryId", searchedTag.getCategory_id());
        model.addAttribute("searchedTagCategoryName", categoryService.findById(searchedTag.getCategory_id()).getName());

        return path.substring(1);
    }

    @PostMapping("/tag/add")
    @ResponseBody
    public Map<String, Object> tagAdd(@ModelAttribute Tag tag, HttpSession httpSession) {
        Map<String, Object> response = new HashMap<>();
        response.put("url", "/tag");
        System.out.println("tagAdd 호출 - name: " + tag.getName() + ", category_id: " + tag.getCategory_id());

        List<Tag> selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");
        if (selectedTags == null) {
            selectedTags = new ArrayList<>();
        }

        // 최대 개수 제한 먼저 검사
        if (selectedTags.size() >= 5) {
            response.put("sizeOverError", "태그는 최대 5개까지 담을 수 있습니다");
            return response;
        }

        // DB에서 태그 검색
        Tag addTag = tagRepository.searchTag(tag);
        Tag tagToAdd;

        if (addTag != null) {
            // 기존 태그라면 색상 세팅
            String tagColor = categoryService.findById(addTag.getCategory_id()).getColor();
            addTag.setColor(tagColor);
            tagToAdd = addTag;
        } else {
            // 신규 태그 추가 후 검색해서 색상 세팅
            int result = tagRepository.addTag(tag);
            tag = tagRepository.searchTag(tag);
            tag.setColor(categoryService.findById(tag.getCategory_id()).getColor());
            tagToAdd = tag;
            response.put("result", result);
        }

        // 중복 검사 후 추가
        if (selectedTags.contains(tagToAdd)) {
            response.put("alreadyInList", "이미 목록에 추가된 태그입니다.");
        } else {
            selectedTags.add(tagToAdd);
            response.put("addTag", tagToAdd);
        }

        httpSession.setAttribute("selectedTags", selectedTags);
        System.out.println("현재 selectedTags: " + selectedTags);

        return response;
    }



    @PostMapping("/tag/remove")
    @ResponseBody
    public Map<String, Object> tagRemove(@RequestBody Tag tag, HttpSession httpSession) {
        Map<String, Object> response = new HashMap<>();
        response.put("url", "/tag");

        List<Tag> selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");

        if (selectedTags == null) {
            response.put("tagNotFound", "삭제할 태그 목록이 비어 있습니다");
            return response;
        }

        boolean removed = selectedTags.removeIf(t ->
                t.getId().equals(tag.getId())
                        && t.getCategory_id().equals(tag.getCategory_id())
                        && t.getName().equals(tag.getName()));

        if (removed) {
            httpSession.setAttribute("selectedTags", selectedTags);
            response.put("deleteSuccess", "삭제 성공");
        } else {
            response.put("tagNotFound", "삭제하려는 태그를 찾을 수 없습니다");
        }

        return response;
    }


//    @PostMapping("/tag/save")
//    public void tagSave(List<Tag> tags, HttpSession httpSession) {
//        // 저장 로직 작성 필요
//    }

    @PostMapping("/tag/search")
    @ResponseBody
    public ResponseEntity<Tag> searchTag(
            @RequestParam("name") String name,
            @RequestParam("category_id") Long category_id
    ) {
        Tag tag = new Tag();
        tag.setName(name.trim());
        tag.setCategory_id(category_id);

        Tag searchedTag = tagRepository.searchTag(tag);
        if (searchedTag == null) {
            return ResponseEntity.notFound().build();
        }

        String color = categoryService.findById(searchedTag.getCategory_id()).getColor();
        searchedTag.setColor(color);

        return ResponseEntity.ok(searchedTag);
    }

    @PostMapping("/tag/save")
    @ResponseBody
    public Map<String, Object> tagsAdd(@RequestBody Tag tag, HttpSession httpSession) {

        Map<String, Object> response = new HashMap<>();
        response.put("url", "/tag");

        List<Tag> selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");
        if (selectedTags == null) {
            selectedTags = new ArrayList<>();
        }

        // 최대 개수 제한 먼저 검사
        if (selectedTags.size() >= 5) {
            response.put("sizeOverError", "태그는 최대 5개까지 담을 수 있습니다");
            return response;
        }

        // DB에서 태그 검색
        Tag addTag = tagRepository.searchTag(tag);
        Tag tagToAdd;

        if (addTag != null) {
            // 기존 태그라면 색상 세팅
            String tagColor = categoryService.findById(addTag.getCategory_id()).getColor();
            addTag.setColor(tagColor);
            tagToAdd = addTag;
        } else {
            // 신규 태그 추가 후 검색해서 색상 세팅
            int result = tagRepository.addTag(tag);
            tag = tagRepository.searchTag(tag);
            tag.setColor(categoryService.findById(tag.getCategory_id()).getColor());
            tagToAdd = tag;
            response.put("result", result);
        }

        // 중복 검사 후 추가
        if (selectedTags.contains(tagToAdd)) {
            response.put("alreadyInList", "이미 목록에 추가된 태그입니다.");
            return response;
        } else {
            selectedTags.add(tagToAdd);
            response.put("addTag", tagToAdd);
        }

        httpSession.setAttribute("selectedTags", selectedTags);
        System.out.println("현재 selectedTags: " + selectedTags);

        return response;
    }


    @Autowired
    TagValidator tagValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(tagValidator);
    }
}
