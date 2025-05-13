package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority; // Spring Security 권한 인터페이스 import
import org.springframework.security.core.userdetails.UserDetails; // Spring Security 사용자 정보 인터페이스 import

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    private Long id;

    //사용자 기본 정보
    private String username; // 사용자 아이디 (로그인 시 사용)
    private String name; // 닉네임 (화면에 표시)
    private String password; // 비밀번호
    // private String re_password; // 비밀번호 확인 용도는 DB 도메인에 포함 X

    private String juminNo; // 주민등록번호
    private LocalDateTime createdAt; // 가입일시

    // OAuth 사용자용
    private String provider; // OAuth 로그인 제공자
    private String providerId; // OAuth 제공자의 사용자 고유 ID

    //사용자 현재 위치
    private double latitude;
    private double longitude;
    private String areaName; // 행정구역명

    //계정상태
    private String status; // 계정 상태 (DB: ENUM 'active', 'paused', 'banned')

    //나이, 성별 (ERD user 테이블 칼럼은 아님. 주민번호 기반 계산)
    private Integer age;
    private String gender;

    // 사용자가 가지고 있는 권한 목록
    private List<Authority> authorities; // GrantedAuthority 를 구현한 Authority 객체 리스트 (또는 문자열 리스트)

    /**
     * 사용자의 권한 목록을 반환
     * Spring Security 가 인증 및 인가 시 이 메소드를 사용하여 사용자의 권한 정보 얻기
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities; // List<Authority> 객체를 Collection<GrantedAuthority> 로 반환
    }


    /**
     * 사용자의 비밀번호를 반환합니다.
     * Spring Security 가 인증 시 입력된 비밀번호와 비교하기 위해 사용합니다.
     */
    @Override
    public String getPassword() {
        return this.password;
    }


    /**
     * 사용자의 사용자 이름(ID)을 반환합니다.
     * Spring Security 가 인증 시 사용자를 식별하기 위해 사용
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    // ------------------------------------------------------------------------------------------------

    /**
     * 계정 만료 여부를 반환
     * 실제 계정 만료 기능이 없으므로 항상 true 반환.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠금 여부를 반환
     * 실제 계정 잠금 기능이 없으므로 항상 true 반환.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명(비밀번호) 만료 여부를 반환
     * 실제 비밀번호 만료 정책이 없으므로 항상 true 반환.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 사용자 활성화 여부를 반환 (활성화되어 있으면 true)
     * ERD 의 `status` 칼럼 값에 따라 구현
     * 'active' 상태일 때 true 를 반환하도록 구현
     */
    @Override
    public boolean isEnabled() {
        // this.status 필드가 null 이 아니면서, 그 값이 "active" 인 경우에만 true 반환
        return this.status != null && this.status.equals("active");

        // 'banned' 상태일 때는 당연히 false 가 반환
        // null 인 경우도 false 가 반환
    }


}