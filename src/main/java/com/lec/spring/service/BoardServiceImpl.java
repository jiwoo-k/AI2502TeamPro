package com.lec.spring.service;

import com.lec.spring.domain.Post;
import com.lec.spring.repository.PostRepository;
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
    public BoardServiceImpl(SqlSession sqlSession) {
        System.out.println("boardServiceImpl");
        this.postRepository = sqlSession.getMapper(PostRepository.class);
        this.userRepository = sqlSession.getMapper(UserRepository.class);
    }
    @Override
    public int write(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post detail(Long id) {
        System.out.println("잠깐만" + id);
        return postRepository.findById(id);
    }

    @Override
    public List<Post> list() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> list(Integer page, Model model) {
        return List.of();
    }

    @Override
    public int update(Post post) {
        return postRepository.update(post);
    }
    @Transactional
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

    @Override
    public List<Post> listByType(String type) {
        return postRepository.findByType(type);
    }

}
