package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//  Spring Security 의 GrantedAuthority 인터페이스를 구현
public class Authority implements GrantedAuthority {

    private Long id;
    private String name;

    // GrantedAuthority 인터페이스 구현 메소드
    @Override
    public String getAuthority() {
        return this.name; // Authority 객체의 name 필드 값을 권한 문자열로 사용
    }
}