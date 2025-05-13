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
public class User{

    private Long id;

    //사용자 기본 정보
    private String username; // 사용자 아이디 (로그인 시 사용)
    private String name; // 닉네임 (화면에 표시)
    private String password; // 비밀번호
    private String re_password; // 비밀번호 확인 용도는 DB 도메인에 포함 X

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
}