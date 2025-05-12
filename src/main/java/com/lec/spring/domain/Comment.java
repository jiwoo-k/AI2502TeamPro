package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DateTimeException;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    private Long id;
    private Long userId;
    private Long postId;
    private Long parentCmtId;
    private String content;
    private LocalDateTime regDate;                                                                    v
}
