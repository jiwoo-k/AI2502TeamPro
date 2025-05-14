package com.lec.spring.domain;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class QryResult {
    private int count;
    private String status;
    private String message;
}
