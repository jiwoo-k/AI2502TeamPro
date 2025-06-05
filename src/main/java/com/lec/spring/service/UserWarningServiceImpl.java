package com.lec.spring.service;

import com.lec.spring.domain.Post;
import com.lec.spring.domain.UserWarning;
import com.lec.spring.repository.PostRepository;
import com.lec.spring.repository.UserWarningRepository;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserWarningServiceImpl implements UserWarningService {

    private final UserWarningRepository userWarningRepository;
    private final PostRepository postRepository;

    /**
     * 생성자에서 SqlSession 을 주입받고, 이를 통해 MyBatis Mapper b 로부터
     * UserWarningRepository 구현체를 획득
     */
    public UserWarningServiceImpl(SqlSession sqlSession) {
        this.userWarningRepository = sqlSession.getMapper(UserWarningRepository.class);
        this.postRepository = sqlSession.getMapper(PostRepository.class);
    }

    @Override
    public int report(UserWarning warning) {
        Post post = postRepository.findById(warning.getPostId());
        if (post == null) {
            return 0;
        }
        return userWarningRepository.insert(warning);
    }

    @Override
    public List<UserWarning> getWarningsByPostId(Long postId) {
        return userWarningRepository.findByPostId(postId);
    }

    @Override
    public List<UserWarning> getWarningsByComplaintUserId(Long complaintUserId) {
        return List.of();
    }

    @Override
    public int postWarningCount(Long postId) {
        return userWarningRepository.postWarningCount(postId);
    }

    @Override
    public List<UserWarning> findWarningDetails(Long userId) {
        return userWarningRepository.findWarningDetails(userId);
    }
}

