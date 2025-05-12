package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserWarning {
    //신고한 사용자
    private Long complaint_userid;
    //신고당한 사용자
    private User offender_userid;
}