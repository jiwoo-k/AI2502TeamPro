package com.lec.spring.service;

import com.lec.spring.domain.UserWarning;
import com.lec.spring.repository.UserWarningRepository;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserWarningServiceImpl implements UserWarningService {

    private final UserWarningRepository repository;

    /**
     * 생성자에서 SqlSession 을 주입받고, 이를 통해 MyBatis Mapper b 로부터
     * UserWarningRepository 구현체를 획득
     */
    public UserWarningServiceImpl(SqlSession sqlSession) {
        this.repository = sqlSession.getMapper(UserWarningRepository.class);
    }

    @Override
    public int report(UserWarning warning) {
        return repository.insert(warning);
    }

    @Override
    public List<UserWarning> getWarningsByPostId(Long postId) {
        return repository.findByPostId(postId);
    }

    @Override
    public List<UserWarning> getWarningsByComplaintUserId(Long complaintUserId) {
        return repository.findByComplaintUserId(complaintUserId);
    }

}
