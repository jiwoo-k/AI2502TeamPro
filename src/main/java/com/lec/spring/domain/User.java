package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority; // Spring Security 권한 인터페이스 import
import org.springframework.security.core.userdetails.UserDetails; // Spring Security 사용자 정보 인터페이스 import

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private Long id;

    //사용자 기본 정보
    private String username; // 사용자 아이디 (로그인 시 사용)
    private String name; // 닉네임 (화면에 표시)
    private String password; // 비밀번호
    private String re_password; // 비밀번호 확인 용도는 DB 도메인에 포함 X

    private LocalDateTime createdAt; // 가입일시

    // OAuth 사용자용
    private String provider; // OAuth 로그인 제공자
    private String providerId; // OAuth 제공자의 사용자 고유 ID

    //사용자 현재 위치
    private Double latitude;
    private Double longitude;
    private String areaName; // 행정구역명

    //계정상태
    private String status; // 계정 상태 (DB: ENUM 'active', 'paused', 'banned')

    //나이, 성별 (ERD user 테이블 칼럼은 아님. 주민번호 기반 계산)
    private Integer age;
    private String gender;

    private int followersCount;
    private int reportCount;

    private Boolean following;

    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getTagsAsString() {
        if (tags == null || tags.isEmpty()) return "";
        return String.join(",", tags);
    }


    /*public int getAge() {
        if (juminNo == null || juminNo.length() != 13) return -1;
        int birthYear = Integer.parseInt(juminNo.substring(0, 2));
        int birthMonth = Integer.parseInt(juminNo.substring(2, 4));
        int birthDay = Integer.parseInt(juminNo.substring(4, 6));
        int centuryCode = Integer.parseInt(juminNo.substring(6, 7));

        int fullYear;
        if (centuryCode == 1 || centuryCode == 2) {
            fullYear = 1900 + birthYear;
        } else if (centuryCode == 3 || centuryCode == 4) {
            fullYear = 2000 + birthYear;
        } else if (centuryCode == 5 || centuryCode == 6) {
            fullYear = 1900 + birthYear; // 외국인
        } else {
            return -1; // 오류 처리
        }

        LocalDate birth = LocalDate.of(fullYear, birthMonth, birthDay);
        LocalDate now = LocalDate.now();

        int diff = now.getYear() - birth.getYear();

//       return Period.between(birth, LocalDate.now()).getYears(); // 만나이
//       return diff; // 연나이
        return diff + 1; // 세는나이
    }


    public String getGender() {
        if (juminNo == null || juminNo.length() != 13) return "Unknown";
        int genderCode = Integer.parseInt(juminNo.substring(6, 7));
        return switch (genderCode) {
            case 1, 3, 5, 7, 9 -> "남자";
            case 2, 4, 6, 8, 0 -> "여자";
            default -> "Unknown";
        };
    }*/

}