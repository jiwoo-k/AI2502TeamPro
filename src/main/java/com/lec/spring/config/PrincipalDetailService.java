package com.lec.spring.config;

import com.lec.spring.domain.User;
import com.lec.spring.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
        User user = userService.findByUsername(username);

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
        throw new UsernameNotFoundException(username);
    }
}
