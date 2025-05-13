package com.lec.spring.config;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import com.lec.spring.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PrincipalDetails implements UserDetails {
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public User user;

    // 로그인한 사용자 정보 가져오기
    public User getUser() {
        return user;
    }

    //일반 로그인용 생성자
    public PrincipalDetails(User user) {
        System.out.println("😁UserDetails(user) 생성: " + user);
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("🎁getAuthorities() 호출");

        Collection<GrantedAuthority> collect = new ArrayList<>();

        //DB 에서 user의 권한(들) 읽어오기
        List<Authority> list = userService.selectAuthoritiesByUserId(user.getId());

        for(Authority auth : list){
//            collect.add(() -> auth.getName());
            collect.add(new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return auth.getName();
                }

                //thymeleaf 등에서 활용할 문자열
                @Override
                public String toString() {
                    return auth.getName();
                }
            });
        }

        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
