package com.lec.spring.service;

import com.lec.spring.domain.Post;
import org.springframework.ui.Model;

import java.util.List;

public interface BoardService {
    // 게시판 작성하기
    int write (Post post);

    // id 가져와서 글 읽기
    Post detail (Long id);

    // 게시판 목록
    List<Post> list ();

    // 페이징
    List<Post> list (Integer page, Model model);

    // 게시판 수정하기
    int update(Post post);

    // 게시판 삭제
    int delete(Long id);
}
