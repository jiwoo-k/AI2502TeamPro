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
    private final PostRepository boardRepository;
    private final UserRepository userRepository;
    public BoardServiceImpl(SqlSession sqlSession) {
        this.boardRepository = sqlSession.getMapper(PostRepository.class);
        this.userRepository = sqlSession.getMapper(UserRepository.class);
    }
    @Override
    public int write(Post board) {
        return boardRepository.save(board);
    }

    @Override
    public Post detail(Long id) {
        return boardRepository.findById(id);
    }

    @Override
    public List<Post> list() {
        return boardRepository.findAll();
    }

    @Override
    public List<Post> list(Integer page, Model model) {
        return List.of();
    }

    @Override
    public int update(Post board) {
        return boardRepository.update(board);
    }

    @Override
    public int delete(Long id) {
        int result = 0;
        boardRepository.findById(id);
        Post board = boardRepository.findById(id);
        if (board != null) {
            result = boardRepository.delete(id);
        }
        return result;
    }
}
