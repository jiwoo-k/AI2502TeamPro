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
    public User findByUserId(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public boolean isExistUserName(String username) {
        User user = findByUsername(username);
        return (user != null);
    }

    @Override
    public boolean isExistName(String name) {
        User user = findByName(name);
        return (user != null);
    }

    @Override
    public int register(User user) {
        user.setUsername(user.getUsername().toUpperCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

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

    @Override
    public int saveUserLoginHistory(Long id) {
        return userRepository.saveLogin(id);
    }
}
