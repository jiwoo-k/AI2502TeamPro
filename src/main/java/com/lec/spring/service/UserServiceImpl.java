package com.lec.spring.service;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.LoginHistory;
import com.lec.spring.domain.User;
import com.lec.spring.repository.AuthorityRepository;
import com.lec.spring.repository.UserRepository;
import com.lec.spring.util.U;
import org.apache.ibatis.session.SqlSession;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    //해당 username 의 사용자 존재하는가?
    @Override
    public boolean isExistUserName(String username) {
        User user = findByUsername(username);
        return (user != null);
    }

    // 해당 닉네임의 사용자 존재하는가?
    @Override
    public boolean isExistName(String name) {
        User user = findByName(name);
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

    @Override
    public int saveUserLoginHistory(Long id) {
        User user = userRepository.findById(id);
        return userRepository.saveLogin(id);
    }

    @Override
    public int updateLocation(User user) {
        return userRepository.updateLocation(user);
    }

    @Override
    public List<User> findNearUsers() {
        return userRepository.findNearUsers();
    }

    @Override
    public List<User> findUsersByWarnCount(Integer warnCount1, Integer warnCount2) {
        return userRepository.findUsersByWarnCount(warnCount1, warnCount2);
    }

    @Override
    @Transactional
    public void limitUser(Long id, Integer days) {
        User user = userRepository.findById(id);

        //계쩡 정지만료일, status 설정

        //days 가 안넘어오면 밴
        if(days == null) {
            user.setStatus("banned");
            user.setPauseEndDate(null);
        }
        else{
            user.setStatus("paused");
            user.setPauseEndDate(LocalDateTime.now().plusDays(days));
        }


        userRepository.updateState(user);
    }

    @Transactional // 중요: DB 변경이 발생하므로 트랜잭션 처리
    @Override
    public User activateAccount(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        // 임시 정지 상태이고, 정지 만료일이 지났다면 계정 활성화
        if(user != null){
            if (user.getStatus().equals("paused") &&
                    user.getPauseEndDate() != null &&
                    user.getPauseEndDate().isBefore(LocalDateTime.now())) {

                user.setStatus("active");
                user.setPauseEndDate(null);
                userRepository.updateState(user);
            }
        }
        return user;
    }

    @Override
    public List<LoginHistory> findLoginHistory(LocalDate startDate, LocalDate EndDate) {
        return userRepository.findLoginHistory(startDate, EndDate);
    }
}
