package com.lec.spring.config;

import com.lec.spring.domain.User;
import com.lec.spring.service.UserService;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PrincipalDetailService implements UserDetailsService {
    private final UserService userService;

    public PrincipalDetailService(UserService userService) {
        this.userService = userService;
    }

    //얘의 리턴값이 authentication 의 principal 에 들어감
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🧨loadUserByUsername(" + username + ") 호출");
        //DB 조회
        /*User user = userService.findByUsername(username);

        //해당 username 의 User 가 DB 에 있다면
        //UserDetail 을 생성해서 리턴!
        if(user != null){
            PrincipalDetails userDetails = new PrincipalDetails(user);
            userDetails.setUserService(userService);
            return userDetails;
        }

        // 해당 username 의 user 가 없다면?
        // UsernameNotFoundException 을 throw 해주어야 한다.
        //AuthenticationFailureHandler 가 호출되게 된다!
        throw new UsernameNotFoundException(username);*/

        User user = userService.activateAccount(username);

        //해당 username 의 User 가 DB 에 있다면
        //UserDetail 을 생성해서 리턴!
        if(user != null){

            if (user.getPauseEndDate() != null) {
                System.out.println("🧨 사용자 정지 만료일: " + user.getPauseEndDate());
                System.out.println("🧨 현재 시간: " + LocalDateTime.now());
                System.out.println("🧨 정지 만료일이 현재 이후인가? " + user.getPauseEndDate().isAfter(LocalDateTime.now()));
            }

            //status 가 banned
            if(user.getStatus().equals("banned")){
                throw new DisabledException("영구 정지된 계정입니다.");
            }
            else if(user.getStatus().equals("paused") &&
                    user.getPauseEndDate() != null &&
                    user.getPauseEndDate().isAfter(LocalDateTime.now()))
            {
                String formattedEndDate = user.getPauseEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                throw new LockedException("계정이 " + formattedEndDate + "까지 일시 정지되었습니다.");
            }


            PrincipalDetails userDetails = new PrincipalDetails(user);
            userDetails.setUserService(userService);
            return userDetails;
        }
        else{
            throw new UsernameNotFoundException("존재하지 않는 아이디입니다.");
        }

    }
}
