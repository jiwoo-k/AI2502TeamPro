package com.lec.spring.repository;


import com.lec.spring.domain.Post;
import com.lec.spring.domain.Tag;
import com.lec.spring.domain.User;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository {
    // 작성한 글 저장하기
    int save (Post post);

    // 특정 id 내용 뽑아오기
    Post findById(Long id);

    // 전체 게시판 목록 가져오기
    List<Post> findAll();

    // 게시판 수정한거 업데이트 하기
    int update(Post post);

    // 게시판 삭제하기
    int deleteById(Long id);

    // 페이징
    List<Post> selectPage(int page , int rows);

    // 전체 글의 개수
    int countAll();

    // 타입 선택 기능 추가
    List<Post> findByType(String type);

    //타입 + 위치 거리 기준 추가
    List<Post> findByTypeLocation(@Param("type") String type, @Param("userList") List<User> users);

    // 선택한 태그들 가져오기 (손님)
    List<Tag> findTagsByPostId(Long post_id);

    // 선택한 태그들 가져오기 (도우미)
//    List<Tag> findTagsByUserId(Long user_id);

    // 신고하기
    int warningOn(Boolean warning);

    // 삭제 여부
    int isDelete (Long id);

    // 삭제 날짜
    int deletedAt (Long id, LocalDateTime now);

    // 페이징
    // from 부터 rows 개 만큰 SELECT
    List<Post> selectFromRow(int from, int rows);
}
