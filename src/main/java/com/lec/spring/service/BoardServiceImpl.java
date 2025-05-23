package com.lec.spring.service;

import com.lec.spring.domain.Attachment;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.Tag;
import com.lec.spring.domain.User;
import com.lec.spring.repository.*;
import com.lec.spring.util.U;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class BoardServiceImpl implements BoardService {

    @Value("${app.upload.path}")
    private String uploadDir;

    @Value("${app.pagination.write_pages}")
    private int WRITE_PAGES;

    @Value("${app.pagination.page_rows}")
    private int PAGE_ROWS;

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserFollowingRepository userFollowingRepository;
    private final AttachmentRepository attachmentRepository;
    private final TagRepository tagRepository;

    public BoardServiceImpl(SqlSession sqlSession) {
        System.out.println("[ACTIVE] BoardServiceImpl");
        this.postRepository = sqlSession.getMapper(PostRepository.class);
        this.userRepository = sqlSession.getMapper(UserRepository.class);
        this.userFollowingRepository = sqlSession.getMapper(UserFollowingRepository.class);
        this.attachmentRepository = sqlSession.getMapper(AttachmentRepository.class);
        this.tagRepository = sqlSession.getMapper(TagRepository.class);
    }

    @Override
    public int write(Post post) {
        return postRepository.save(post);
    }

    // 특정 id 의 글 조회
    // 트랜잭션 처리
    @Override
    @Transactional  // <- 이 메소드를 트랜잭션 처리.
    public Post detail(Long id) {
        System.out.println("PostId : " + id);
        Post post = postRepository.findById(id); // SELECT

        Integer followCount = userFollowingRepository.followCount(post.getUser_id());
        post.setFollowCount(followCount);

        if (post != null) {
            // 첨부파일(들) 정보 가져오기
            List<Attachment> fileList = attachmentRepository.findByPost(post.getId());
            attachmentRepository.setImage(fileList);  // '이미지 파일 여부' 세팅
            post.setFileList(fileList);

            // postTag 주입
            List<Tag> postTag = postRepository.findTagsByPostId(id);
            post.setPost_tag(postTag);

//            // user_tag 주입
//            if ("helper".equals(post.getType())) {
//                List<Tag> userTag = postRepository.findTagsByUserId(id);
//                List <Tag> userTag = postRepository.findTagsByPostId(id);
//                System.out.println("userTag" + userTag);
//                post.setUser_tag(userTag);
//
//            }
        }
        return post;
    }


    @Override
    public int write(Post post, Map<String, MultipartFile> files) {
        // 현재 로그인한 작성자 정보
        User user = U.getLoggedUser();

        // 위 정보는 session 의 정보이고, 디시 DB 에서 읽어온다.
        user = userRepository.findById(user.getId());
        post.setUser(user);  // 글 작성자 세팅.

        int cnt = postRepository.save(post);   // 글 먼저 저장 - 그래야 AI 된 PK값(id) 를 받아온다.

        // 첨부파일 추가.
        attachmentRepository.addFiles(files, post.getId());

        return cnt;
    }


    // 이거 혹시 몰라서 남겨둠
    @Override
    public List<Post> list() {
        return postRepository.findAll();
    }

    // 페이징 부분 (페이징 리스트)
        // page : 현재 페이지 (1-base)
        @Override
        public List<Post> list(Integer page, Model model) {
            // 현재 페이지 , 디폴트는 1
            if(page == null) page = 1;
            if(page < 1) page = 1;

            // 페이징
            // writePages: 한 [페이징] 당 몇개의 페이지가 표시되나
            // pageRows: 한 '페이지'에 몇개의 글을 리스트 할것인가?
            HttpSession session = U.getSession();
            Integer writePages = (Integer)session.getAttribute("writePages");
            if(writePages == null) writePages = WRITE_PAGES;   // 만약 session 에 없으면 기본값으로 동작
            Integer pageRows = (Integer)session.getAttribute("pageRows");
            if(pageRows == null) pageRows = PAGE_ROWS;   // session 에 없으면 기본값으로
            session.setAttribute("page", page);   // 현재 페이지 번호 -> session 에 저장

            long cnt = postRepository.countAll(); // 글 목록 전체의 개수
            int totalPage = (int)Math.ceil(cnt / (double)pageRows);  // 총 몇 '페이지' 분량인가

            // [페이징] 에 표시할 '시작페이지' 와 '마지막페이지'
            int startPage = 0;
            int endPage = 0;

            // 해당 '페이지'의 글 목록
            List<Post> list = null;

            if(cnt > 0){
                // page 값 보정
                if(page > totalPage) page = totalPage;

                // 몇번째 데이터부터 fromRow
                int fromRow = (page - 1) * pageRows;

                // [페이징] 에 표시할 '시작페이지' 와 '마지막페이지' 계산
                startPage = (((page - 1) / writePages) * writePages) + 1;
                endPage = startPage + writePages - 1;
                if (endPage >= totalPage) endPage = totalPage;

                // 해당 page 의 글 목록 읽어오기
                list = postRepository.selectFromRow(fromRow, pageRows);
                model.addAttribute("list", list);
            } else {
                page = 0;
            }

            model.addAttribute("cnt", cnt);  // 전체 글 개수
            model.addAttribute("page", page); // 현재 페이지
            model.addAttribute("totalPage", totalPage);  // 총 '페이지' 수
            model.addAttribute("pageRows", pageRows);  // 한 '페이지' 에 표시할 글 개수

            // [페이징]
            model.addAttribute("url", U.getRequest().getRequestURI());  // 목록 url
            model.addAttribute("writePages", writePages); // [페이징] 에 표시할 숫자 개수
            model.addAttribute("startPage", startPage);  // [페이징] 에 표시할 시작 페이지
            model.addAttribute("endPage", endPage);   // [페이징] 에 표시할 마지막 페이지


            return list;
        }

    @Override
    public int update(Post post) {
        return postRepository.update(post);
    }

    @Override
    public int update(Post post, Map<String, MultipartFile> files, Long[] delFile) {
        int result = 0;
        result = postRepository.update(post);

        attachmentRepository.addFiles(files, post.getId());

        if (delFile != null) {
            for (Long fileId : delFile) {
                Attachment file = attachmentRepository.findById(fileId);
                if (file != null) {
                    attachmentRepository.delFiles(file);
                    attachmentRepository.delete(file);
                }
            }
        }

        return result;
    }

    @Override
    public int delete(Long id) {
        int result = 0;
        System.out.println("PostId : " + id);
        Post post = postRepository.findById(id);
        if (post != null) {
            result = postRepository.deleteById(id);
        }
        return result;
    }

    // 태그 선택 기능 추가
    @Override
    public List<Post> listByType(String type) {
        List<Post> posts = postRepository.findByType(type);

        for (Post post : posts) {
            Long postId = post.getId();

            List<Attachment> fileList = attachmentRepository.findByPost(postId);
            attachmentRepository.setImage(fileList); // 이미지 여부 설정
            post.setFileList(fileList);

            // post_tag 주입
            List<Tag> postTags = postRepository.findTagsByPostId(postId);
            post.setPost_tag(postTags);
//
//            // user_tag는 helper만 조회
//            if ("helper".equals(post.getType())) {
//                List<Tag> userTags = postRepository.findTagsByUserId(postId);
//                post.setUser_tag(userTags);
//            }

            // 팔로우 수 주입
            Integer followCount = userFollowingRepository.followCount(post.getUser_id());
            post.setFollowCount(followCount);
        }

        return posts;
    }

    @Override
    public List<Post> listByTypeLocation(String type, List<User> users) {
        List<Post> posts = postRepository.findByTypeLocation(type, users);

        for (Post post : posts) {
            Long postId = post.getId();

            // post_tag 주입
            List<Tag> postTags = postRepository.findTagsByPostId(postId);
            post.setPost_tag(postTags);

            // 팔로우 수 주입
            Integer followCount = userFollowingRepository.followCount(post.getUser_id());
            post.setFollowCount(followCount);
        }

        return posts;
    }

    @Transactional
    public void deleteTime(Long id) {
        // 삭제한건 값이 1
        postRepository.isDelete(id);
        postRepository.deletedAt(id, LocalDateTime.now());
    }



    @Override
    public List<Tag> postTagList(Long post_id) {
        //특정 게시물의 태그목록을 가져오자
        return postRepository.findTagsByPostId(post_id);
    }
}
