package com.lec.spring.repository;


import com.lec.spring.domain.Post;

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
}
