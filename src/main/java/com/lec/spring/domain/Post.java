package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// user_id 는 로그인 사용자를 구현해야 서버가 돌아가기 때문에 일단 1로 고정해둠
public class Post {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdat;
    private Long user_id = 1L;
    private String type;
    private Boolean isdeleted;
    private LocalDateTime deletedat;
    private String name;
}
