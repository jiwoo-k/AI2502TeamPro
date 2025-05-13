package com.lec.spring.service;

import com.lec.spring.domain.Post;
import com.lec.spring.repository.PostRepository;
import com.lec.spring.repository.UserRepository;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
@Service
public class BoardServiceImpl implements BoardService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    public BoardServiceImpl(SqlSession sqlSession) {
        this.postRepository = sqlSession.getMapper(PostRepository.class);
        this.userRepository = sqlSession.getMapper(UserRepository.class);
    }
    @Override
    public int write(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post detail(Long id) {
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

    @Override
    public int delete(Long id) {
        int result = 0;
        postRepository.findById(id);
        Post post = postRepository.findById(id);
        if (post != null) {
            result = postRepository.delete(id);
        }
        return result;
    }
}
