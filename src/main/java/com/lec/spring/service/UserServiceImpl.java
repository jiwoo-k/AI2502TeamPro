package com.lec.spring.service;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import com.lec.spring.repository.AuthorityRepository;
import com.lec.spring.repository.UserRepository;
import org.apache.ibatis.session.SqlSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    //필요한 것들 생성자에서 주입
    public UserServiceImpl(SqlSession sqlSession, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = sqlSession.getMapper(UserRepository.class);
        this.authorityRepository = sqlSession.getMapper(AuthorityRepository.class);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    //해당 username 의 사용자 존재하는가?
    @Override
    public boolean isExist(String username) {
        User user = findByUsername(username);
        return (user != null);
    }

    //회원가입과 동시에 권한 부여
    @Override
    public int register(User user) {
        user.setUsername(user.getUsername().toUpperCase()); //DB 에는 대문자로 저장
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user); //새로 회원(User) 저장. id 값 받아옴

        // 신규 회원은 ROLE_MEMBER 권한 기본적으로 부여
        Authority auth = authorityRepository.findByName("ROLE_MEMBER");

        Long userId = user.getId();
        Long authId = auth.getId();
        authorityRepository.addAuthority(userId, authId);

        return 1;
    }

    @Override
    public List<Authority> selectAuthoritiesByUserId(Long id) {
        User user = userRepository.findById(id);
        return authorityRepository.findByUser(user);
    }
}
