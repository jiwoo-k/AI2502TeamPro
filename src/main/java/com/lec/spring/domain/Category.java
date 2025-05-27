package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    private Long id;
    private String name;
    private String color;

    //DB 에는 없지만 정렬 및 집계를 위해서..
    private Long tagCount;
    private Long postCount;
}
