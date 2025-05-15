package com.lec.spring.service;

import com.lec.spring.domain.Post;
import com.lec.spring.domain.Tag;
import com.lec.spring.repository.PostRepository;
import com.lec.spring.repository.UserFollowingRepository;
import com.lec.spring.repository.UserRepository;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;
@Service
public class BoardServiceImpl implements BoardService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final UserFollowingRepository userFollowingRepository;
    public BoardServiceImpl(SqlSession sqlSession) {
        System.out.println("boardServiceImpl");
        this.postRepository = sqlSession.getMapper(PostRepository.class);
        this.userRepository = sqlSession.getMapper(UserRepository.class);
        this.userFollowingRepository = sqlSession.getMapper(UserFollowingRepository.class);
    }
    @Override
    public int write(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post detail(Long id) {
        System.out.println("잠깐만" + id);
        Post post = postRepository.findById(id);
        if (post != null) {
            // post)tag 주입
            List<Tag> postTag = postRepository.findByPostTag(id);
            post.setPost_tag(postTag);
            // user_tag 주입
            if ("도우미".equals(post.getType())) {
                List <Tag> usertag = postRepository.findByUserTag(id);
                System.out.println("usertag" + usertag);
                post.setUser_tag(usertag);

            }
        }
        return post;
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
    public int delete(Long id) {
        int result = 0;
        System.out.println("잠깐만 " + id);
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
            Integer followCount = userFollowingRepository.followCount(post.getUser_id());
            post.setCount(followCount);
        }
        return posts;
    }

}
