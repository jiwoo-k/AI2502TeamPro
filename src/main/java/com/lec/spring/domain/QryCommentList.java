package com.lec.spring.domain;  // ✅ 반드시 있어야 함!

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class QryCommentList extends QryResult {

    @JsonProperty("data")
    private List<Comment> list;
}
