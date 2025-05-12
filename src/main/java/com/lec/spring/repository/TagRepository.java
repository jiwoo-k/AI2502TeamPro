package com.lec.spring.repository;

import com.lec.spring.domain.Post;
import com.lec.spring.domain.Tag;
import com.lec.spring.domain.User;

import java.util.List;

public interface TagRepository {
    //특정 사용자의 사용자 태그(들) 가져오기
    List<Tag> findByUser(User user);

    //특정 게시글의 게시글 태그(들) 가져오기
    List<Tag> findByPost(Post post);

    //1. 새로운 태그 저장
    int save(Tag tag);

    //2. 특정 태그가 존재한다면 불러오기
    Tag searchTag(String name, Long category_id);



}