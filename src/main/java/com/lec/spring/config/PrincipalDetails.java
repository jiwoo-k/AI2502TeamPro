package com.lec.spring.config;

import com.lec.spring.domain.Authority;
import com.lec.spring.domain.User;
import com.lec.spring.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User {
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

    //OAuth2 로그인용 생성자
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        System.out.println("""
                🎃UserDetails(user, oauth attributes) 생성:
                user: %s
                attributes: %s
                """.formatted(user, attributes));
        this.user = user;
        this.attributes = attributes;
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

    //-------------OAuth2 용
    // OAuth2User 를 implement 하게 되면 구현할 메소드들

    private Map<String, Object> attributes; // <- OAuth2User 의 getAttributes() 값

    //OAuth2User 를 implement
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public String getName(){
        return null; //이번 예제에서는 사용 안함
    }
}
