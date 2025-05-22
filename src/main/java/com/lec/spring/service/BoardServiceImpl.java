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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
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

    @Override
    public Post detail(Long id) {
        System.out.println("PostId : " + id);
        Post post = postRepository.findById(id);
        if (post != null) {
            // post)tag 주입
            List<Tag> postTag = postRepository.findTagsByPostId(id);
            post.setPost_tag(postTag);
            // user_tag 주입
            if ("helper".equals(post.getType())) {
                List <Tag> usertag = postRepository.findTagsByPostId(id);
                System.out.println("usertag" + usertag);
                post.setUser_tag(usertag);

            }
        }
        return post;
    }


//    @Override
//    public int write(Post post) {
//        return write(post, null);  // 파일 없음으로 호출
//    }

    @Override
       public int write(Post post, Map<String, MultipartFile> files) {
           // 현재 로그인한 작성자 정보
           User user = U.getLoggedUser();

           // 위 정보는 session 의 정보이고, 디시 DB 에서 읽어온다.
           user = userRepository.findById(user.getId());
           post.setUser(user);  // 글 작성자 세팅.

           int cnt = postRepository.save(post);   // 글 먼저 저장 (그래야 AI 된 PK값(id) 를 받아온다.

           // 첨부파일 추가
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
       public int update(Post post, Map<String, MultipartFile> files, Long[] delfile) {
           int result = 0;
           result = postRepository.update(post);

           addFiles(files, post.getId());

           if(delfile != null){
               for(Long fileId : delfile){
                   Attachment file = attachmentRepository.findById(fileId);
                   if(file != null){
                       delfile(file);
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
        if(files == null) return;

        for(Map.Entry<String, MultipartFile> e : files.entrySet()){
            // name="upfile##" 인 경우만 첨부파일 등록. (이유, 다른 웹에디터와 섞이지 않도록... ex: summernote)
            if(!e.getKey().startsWith("upfile")) continue;

            // 첨부파일 정보 출력
            System.out.println("\n첨부파일 정보: " + e.getKey());    // name = 값
            U.printFileInfo(e.getValue());   // MultipartFile 정보
            System.out.println();

            // 물리적인 파일 저장
            Attachment file = upload(e.getValue());

            // 성공하면 DB 에도 저장
            if(file != null){
                file.setPostId(id);   // FK 설정
                attachmentRepository.save(file);   // INSERT
            }

        }
    }

    // 물리적으로 서버에 파일 저장.  중복된 파일 이름 -> rename 처리.
    private Attachment upload(MultipartFile multipartFile) {
        Attachment attachment = null;

        // 담긴 파일이 없으면 pass
        String originalFilename = multipartFile.getOriginalFilename();
        if(originalFilename == null || originalFilename.isEmpty()) return null;

        // 원본 파일명
        String sourceName = StringUtils.cleanPath(originalFilename);

        // 저장할 파일 명
        String fileName = sourceName;

        // 파일이 중복되는지 확인
        File file = new File(uploadDir, fileName);
        if(file.exists()){  // 이미 존재하는 파일명, 중복된다면 다른 이름으로 변경하여 저장.
            // a.txt => a_2378142783946.txt  : time stamp 값을 활용할거다!
            // "a" => "a_2378142783946"  : 확장자 없는 경우

            int pos = fileName.lastIndexOf(".");
            if(pos > -1){  // 확장자가 있는 경우
                String name = fileName.substring(0, pos);  // 파일 '이름'
                String ext = fileName.substring(pos);  // 파일 '.확장자'
                // 중복방지를 위한 새로운 이름
                fileName = name + "_" + System.currentTimeMillis() + ext;
            } else { // 확장자가 없는 파일의 경우.
                fileName += "_" + System.currentTimeMillis();
            }
        }
        // 저장될 파일명
        System.out.println("\tfileName = " + fileName);

        // java.io.*  => java.nio.*
        Path copyOfLocation = Paths.get(new File(uploadDir, fileName).getAbsolutePath());
        System.out.println("\t" + copyOfLocation);

        try {
            Files.copy(
                    multipartFile.getInputStream(),
                    copyOfLocation,
                    StandardCopyOption.REPLACE_EXISTING   // 기본에 존재하면 덮어쓰기.
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        attachment = Attachment.builder()
                .fileName(fileName)  // 저장된 이름
                .sourceName(sourceName)  // 원본 이름.
                .build();

        return attachment;
    }

    // 특정 첨부파일을 물리적으로 삭제
    private void delfile(Attachment file) {
        String saveDirectory = new File(uploadDir).getAbsolutePath();

        File f = new File(saveDirectory, file.getFileName());
        System.out.println("삭제시도 -->" + f.getAbsolutePath());
        if(f.exists()) {
            if (f.delete())
                System.out.println("삭제 성공");
            else
                System.out.println("삭제 실패");
        } else {
            System.out.println("파일이 존재하지 않습니다.");
        }
    }

    @Override
    public List<Tag> postTagList(Long post_id) {
        //특정 게시물의 태그목록을 가져오자
       return postRepository.findTagsByPostId(post_id);
    }
}
