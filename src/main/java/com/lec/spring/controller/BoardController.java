package com.lec.spring.controller;

import com.lec.spring.domain.*;
import com.lec.spring.repository.TagRepository;
import com.lec.spring.service.*;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.Tag;
import com.lec.spring.domain.User;
import com.lec.spring.domain.UserWarning;
import com.lec.spring.service.*;
import com.lec.spring.util.U;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private final UserFollowingService userFollowingService;
    private final UserWarningService userWarningService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final TagRepository tagRepository;
    private final AttachmentService attachmentService;

    public BoardController(BoardService boardService,
                           UserFollowingService userFollowingService,
                           UserWarningService userWarningService,
                           UserService userService,
                           AttachmentService attachmentService,
                           CategoryService categoryService,
                           TagRepository tagRepository
                           ) {
        System.out.println("[ACTIVE] BoardController");
        this.boardService = boardService;
        this.userFollowingService = userFollowingService;
        this.userWarningService = userWarningService;
        this.userService = userService;
        this.attachmentService = attachmentService;
        this.categoryService = categoryService;
        this.tagRepository = tagRepository;
    }

    // 수정, 추가. 삭제의 경우 attr name을 result 로 하였음
    @GetMapping("/write")
    public void write() {
    }


    @PostMapping("/write")
    public String write(
            @RequestParam Map<String, MultipartFile> files,
            @Valid Post post,
            @Valid Tag tag,
            @RequestParam(required = false) String type,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal(expression = "user") User loginUser
    ) {

        // vaildator
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("title", post.getTitle());
            redirectAttributes.addFlashAttribute("content", post.getContent());
            redirectAttributes.addFlashAttribute("name", tag.getName());

            // 카테고리 검색 validator
            List<Category> categoryList = categoryService.list();
            redirectAttributes.addFlashAttribute("categoryList", categoryList);
            redirectAttributes.addFlashAttribute("submittedCategoryId", tag.getCategory_id());


            for (FieldError error : bindingResult.getFieldErrors()) {
                redirectAttributes.addFlashAttribute("error_" + error.getField(), error.getDefaultMessage());
            }
            return "redirect:/board/write";
        }



        // user_id 가지고 오기
        post.setUser_id(loginUser.getId());
        int result = boardService.write(post, files);
        model.addAttribute("result", result);
        model.addAttribute("type", type );
        return "board/writeOk";
    }


    @GetMapping("/list")
    public String list(@RequestParam(required = false) String type,
                       @RequestParam(required = false, defaultValue = "1") Integer page,
                       @RequestParam(required = false) Integer pageRows,
                       @RequestParam(required = false) Boolean follow,
                       Model model,
                       Principal principal,
                       HttpSession httpSession) {

        // 로그인 사용자 ID 추출
        Long loginUserId = null;
        User loginUser;

        //위도, 경도
        Double lat1, lng1;

        if (principal != null) {
            String username = principal.getName();
            loginUser = userService.findByUsername(username);
            loginUserId = loginUser.getId();

            //로그인한 사용자 위치검증
            if(loginUser.getLatitude() == null || loginUser.getLongitude() == null) {
                // 위치 정보 검증 (필요 시 주석처리)
//                model.addAttribute("locationMissing", "위치 정보 없음");
//                return "board/list";
            }
            else{
                lat1 = loginUser.getLatitude();
                lng1 = loginUser.getLongitude();
            }
        }
        else{
            //로그인 안한 사용자 위치검증
            lat1 = (Double) httpSession.getAttribute("lat");
            lng1 = (Double) httpSession.getAttribute("lng");

            if(lat1 == null || lng1 == null){
                // 위치 정보 검증 (필요 시 주석처리)
//                model.addAttribute("locationMissing", "위치 정보 없음");
//                return "board/list";
            }
        }

        //1. 사용자 3km 이내 보여주기
        List<User> allUsers = userService.findNearUsers();
        List<User> filteredUsers = new ArrayList<>();
        for(User user : allUsers){
            Double lat2 = user.getLatitude();
            Double lng2 = user.getLongitude();



            if(lat2 == null || lng2 == null) continue;

            // 거리 계산 (위치 정보 부재 시 주석 처리)
//            double distance = calcDistance(lat1, lat2, lng1, lng2);
//            if(distance <= 3){
//                filteredUsers.add(user);
//            }
        }

        if (type == null || type.isBlank()) {
            type = "guest";
        }

//        List<Post> allPosts = boardService.listByTypeLocation(type, filteredUsers);

        List<Post> allPosts;
        if (filteredUsers.isEmpty()) {
            allPosts = boardService.listByType(type); // ← 일반 조회로 우회
        } else {
            allPosts = boardService.listByTypeLocation(type, filteredUsers);
        }

        model.addAttribute("id", loginUserId);


//        List<Post> allPosts = boardService.listByType(type);

        for (Post post : allPosts) {
            boolean isFollowed = loginUserId != null && userFollowingService.isFollowing(loginUserId, post.getUser_id());
            post.setFollow(isFollowed);
        }

        // 태그 필터링
        List<Post> filteredPosts = new ArrayList<>();
        List<Tag> selectedTags = (List<Tag>) httpSession.getAttribute("selectedTags");

        if (selectedTags == null) {
            selectedTags = new ArrayList<>();
            httpSession.setAttribute("selectedTags", selectedTags);
        }

        if (selectedTags.isEmpty()) {
            model.addAttribute("boardList", allPosts);
        } else {
            for (Post post : allPosts) {
                List<Tag> tags = post.getPost_tag();
                for (Tag tag : selectedTags) {
                    if (tags.contains(tag)) {
                        filteredPosts.add(post);
                        break;
                    }
                }
            }
            model.addAttribute("boardList", filteredPosts);
        }

        // 페이징 처리
        if (pageRows == null || pageRows < 1) {
            pageRows = 10;
        }

        List<Post> displayList;
        if (selectedTags.isEmpty()) {
            displayList = allPosts;
        } else {
            for (Post post : allPosts) {
                List<Tag> tags = post.getPost_tag();
                for (Tag tag : selectedTags) {
                    if (tags.contains(tag)) {
                        filteredPosts.add(post);
                        break;
                    }
                }
            }
            displayList = filteredPosts;
        }

        int totalCnt = displayList.size();
        int totalPage = (int) Math.ceil((double) totalCnt / pageRows);
        if (page < 1) page = 1;
        if (page > totalPage) page = totalPage;

        int fromIndex = (page - 1) * pageRows;
        int toIndex = Math.min(fromIndex + pageRows, totalCnt);
        List<Post> pageList = displayList.subList(fromIndex, toIndex);

        model.addAttribute("boardList", pageList);
        model.addAttribute("cnt", totalCnt);
        model.addAttribute("page", page);
        model.addAttribute("pageRows", pageRows);
        model.addAttribute("totalPage", totalPage);

        int writePages = 10;
        int startPage = ((page - 1) / writePages) * writePages + 1;
        int endPage = Math.min(startPage + writePages - 1, totalPage);

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("url", "/board/list?type=" + type);


        model.addAttribute("follow", (follow != null) ? follow : false);
        model.addAttribute("selectedType", type);
        boardService.list(page, model);

        return "board/list";
    }

    private double calcDistance(Double lat1, Double lat2, Double lng1, Double lng2) {
        double distance;
        int radius = 6371;
        double radian = Math.PI / 180;

        double deltaLat = Math.abs(lat1 - lat2) * radian;
        double deltaLng = Math.abs(lng1 - lng2) * radian;

        double sinDeltaLat = Math.sin(deltaLat / 2);
        double sinDeltaLng = Math.sin(deltaLng / 2);
        double squareRoot = Math.sqrt(
                sinDeltaLat * sinDeltaLat +
                Math.cos(lng1 * radian) * Math.cos(lng2 * radian) * sinDeltaLng * sinDeltaLng);

        distance = 2 * radius * Math.asin(squareRoot);

        return distance;
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
        model.addAttribute("id", loginUserId);


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
        List<UserWarning> warnings = userWarningService.getWarningsByPostId(id);
        boolean hasReported = warnings.stream()
                .anyMatch(w -> w.getComplaintUserId().equals(userId));
        model.addAttribute("hasReported", hasReported);

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


        return "board/updateOk";
    }

    @PostMapping("/delete")
    public String delete(Long id, Model model, @AuthenticationPrincipal(expression = "user") User loginUser) {
        boardService.deleteTime(id);
        model.addAttribute("result", 1);
        return "board/deleteOk";
    }

    @InitBinder("post")
    public void initBinder(WebDataBinder binder) {
        System.out.println("호출 성공");
        binder.setValidator(new BoardValidator());
    }

    // 페이징
    // pageRows 변경시 동작
    @PostMapping("/pageRows")
    public String pageRows(Integer page, Integer pageRows){
        U.getSession().setAttribute("pageRows", pageRows);
        return "redirect:/board/list?page=" + page;
    }


}
