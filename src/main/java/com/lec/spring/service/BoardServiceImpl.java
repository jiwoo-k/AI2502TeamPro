package com.lec.spring.service;

import com.lec.spring.domain.Attachment;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.Tag;
import com.lec.spring.domain.User;
import com.lec.spring.repository.*;
import com.lec.spring.util.U;
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

    @Value("upload")
    private String uploadDir;

    @Value("10")
    private int WRITE_PAGES;

    @Value("10")
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

        if (post != null) {
            // 첨부파일(들) 정보 가져오기
            List<Attachment> fileList = attachmentRepository.findByPost(post.getId());
            setImage(fileList);  // '이미지 파일 여부' 세팅
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
        addFiles(files, post.getId());

        return cnt;
    }


    // 이거 혹시 몰라서 남겨둠
    @Override
    public List<Post> list() {

        return postRepository.findAll();
    }

    // 페이징 부분
    @Override
    public List<Post> list(Integer page, Model model) {
        return List.of();
    }

    @Override
    public int update(Post post) {
        return postRepository.update(post);
    }

    @Override
    public int update(Post post, Map<String, MultipartFile> files, Long[] delFile) {
        int result = 0;
        result = postRepository.update(post);

        addFiles(files, post.getId());

        if (delFile != null) {
            for (Long fileId : delFile) {
                Attachment file = attachmentRepository.findById(fileId);
                if (file != null) {
                    delFiles(file);
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

    @Transactional
    public void deleteTime(Long id) {
        // 삭제한건 값이 1
        postRepository.isDelete(id);
        postRepository.deletedAt(id, LocalDateTime.now());
    }



    // 특정 글(id)  에 첨부파일(들) (files) 추가
    private void addFiles(Map<String, MultipartFile> files, Long id) {
        if (files == null) return;

        for (Map.Entry<String, MultipartFile> e : files.entrySet()) {
            // name="upfile##" 인 경우만 첨부파일 등록. (이유, 다른 웹에디터와 섞이지 않도록... ex: summernote)
            if (!e.getKey().startsWith("upfile")) continue;

            // 첨부파일 정보 출력
            System.out.println("\n첨부파일 정보: " + e.getKey());    // name = 값
            U.printFileInfo(e.getValue());   // MultipartFile 정보
            System.out.println();

            // 물리적인 파일 저장
            Attachment file = upload(e.getValue());

            // 성공하면 DB 에도 저장
            if (file != null) {
                file.setPostId(id);   // FK 설정
                attachmentRepository.save(file);   // INSERT
            }

        }
    }

    private Attachment upload(MultipartFile multipartFile) {
        Attachment attachment = null;

        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) return null;

        String sourceName = StringUtils.cleanPath(originalFilename);

        // 현재 시각 timestamp
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

        // 확장자 분리
        String fileName;
        int pos = sourceName.lastIndexOf(".");
        if (pos > -1) {
            String name = sourceName.substring(0, pos);
            String ext = sourceName.substring(pos); // includes the dot
            fileName = name + "-" + timestamp + ext;
        } else {
            fileName = sourceName + "-" + timestamp;
        }

        System.out.println("\tfileName = " + fileName);

        Path copyOfLocation = Paths.get(new File(uploadDir, fileName).getAbsolutePath());
        System.out.println("\t" + copyOfLocation);

        try {
            Files.copy(
                    multipartFile.getInputStream(),
                    copyOfLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        attachment = Attachment.builder()
                .fileName(fileName)         // 저장된 이름
                .sourceName(sourceName)     // 원본 이름
                .build();

        return attachment;
    }


    // 특정 첨부파일을 물리적으로 삭제
    private void delFiles(Attachment file) {
        String saveDirectory = new File(uploadDir).getAbsolutePath();

        File f = new File(saveDirectory, file.getFileName());
        System.out.println("삭제시도 -->" + f.getAbsolutePath());
        if (f.exists()) {
            if (f.delete())
                System.out.println("삭제 성공");
            else
                System.out.println("삭제 실패");
        } else {
            System.out.println("파일이 존재하지 않습니다.");
        }
    }

    // [이미지 파일 여부 세팅]
    private void setImage(List<Attachment> fileList) {
        // upload 실제 물리적인 경로
        String realPath = new File(uploadDir).getAbsolutePath();

        for (Attachment attachment : fileList) {
            BufferedImage imgData = null;
            File f = new File(realPath, attachment.getFileName());  // 저장된 첨부파일에 대한 File 객체
            try {
                imgData = ImageIO.read(f);
                // ※ ↑ 파일이 존재 하지 않으면 IOExcepion 발생한다
                //   ↑ 이미지가 아닌 경우는 null 리턴
            } catch (IOException e) {
                System.out.println("파일 존재안함: " + f.getAbsolutePath() + " [" + e.getMessage() + "]");
            }

            if (imgData != null) attachment.setImage(true);   // 이미지 여부 체크
        }

    }


    @Override
    public List<Tag> postTagList(Long post_id) {
        //특정 게시물의 태그목록을 가져오자
       return postRepository.findTagsByPostId(post_id);
    }
}
