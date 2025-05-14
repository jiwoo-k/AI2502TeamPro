package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    private Long id;
    private User user_id;
    private Integer type;
    private String title;
    private String content;
    private LocalDateTime createdat;
    private Boolean isdeleted;
    private LocalDateTime deletedat;
}
