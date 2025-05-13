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
public class Post {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdat;
    private User userId;
    private Integer type;
    private Boolean isfinish;
    private LocalDateTime deletedat;
    private int posttypeId;
    private String name;
}
