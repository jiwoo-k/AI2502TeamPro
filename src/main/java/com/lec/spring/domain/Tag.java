package com.lec.spring.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    private Long id;

    private Long category_id;
    private String name;

    //태그가 속한 카테고리의 색을 갖고온다
    private String color;

    // 태그 검색, 태그 id 비교 (session 중복 , 덮어쓰기 문제 때문에 추가함)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name) &&
                Objects.equals(category_id, tag.category_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category_id);
    }
}
