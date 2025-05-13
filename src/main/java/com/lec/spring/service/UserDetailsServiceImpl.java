package com.lec.spring.service;

import com.lec.spring.domain.User;
import com.lec.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // 생성자 주입
    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * 핵심 메소드
     * 사용자가 로그인을 시도할 때, Spring Security 가 호출하는 메소드
     * 사용자가 입력한 아이디(username)를 받아서 데이터베이스에서 해당 사용자의 정보를 조회
     * 조회된 사용자 정보는 UserDetails 객체 (여기서는 여러분의 User 객체) 형태로 반환
     * 만약 해당 아이디의 사용자가 데이터베이스에 없다면 UsernameNotFoundException 을 발생시켜야 함
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. UserRepository 를 사용하여 데이터베이스에서 username 으로 User 정보를 조회
        User user = userRepository.findByUsername(username);

        // 2. 조회된 User 객체가 null 이면 (해당 username 의 사용자가 없으면) 예외 발생
        if (user == null) {
            System.out.println("ERROR: User not found with username: " + username);
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // 3. 조회된 User 객체가 null 이 아니면 (사용자가 존재하면) UserDetails 타입으로 반환
        return user;
    }
}