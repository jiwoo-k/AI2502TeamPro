package com.lec.spring.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.lec.spring.domain.Tag;
import java.time.LocalDateTime;
import java.util.List;

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
    private String type;
    private Boolean isdeleted;
    private LocalDateTime deletedat;
    private String name;
    private User user;

    private Long user_id;

    // 태그 검색
    private List<Tag> post_tag;
    private List<Tag> user_tag;

    // 팔로우
    private Boolean follow;
    private Integer followCount;

}
