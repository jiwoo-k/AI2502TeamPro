package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;

    //사용자 기본 정보
    private String username;
    private String name;
    private String password;
    private String juminNo;
    private LocalDateTime createdAt;

    // OAuth 사용자용
    private String provider;
    private String providerId;

    //사용자 현재 위치
    private double latitude;
    private double longitude;

    //계정상태
    private String status;

    //DB column 은 아니지만 필요한 것들------------------------------------
    //나이, 성별
    private Integer age;
    private String gender;

    //행정구역
    private String areaName;
}
