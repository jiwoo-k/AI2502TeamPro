package com.lec.spring.repository;

import com.lec.spring.domain.Post;
import com.lec.spring.domain.Tag;
import com.lec.spring.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagRepository {
    //1. 새로운 태그 추가하기
    int addTag(Tag tag);

    //2. 특정 태그가 존재한다면 불러오기
    Tag searchTag(String name, Long category_id);

    //담은 태그 목록에 해당하는 게시글(들) 불러오기
    List<Post> findPostsByTags(List<Tag> tags);


    //3. 새로운 게시글 태그(들) 저장
    int savePostTag(List<Tag> tags, Long postId);

    //2. 새로운 사용자 태그 저장
    int saveUserTag(List<Tag> tags, Long UserId);

    //3. 사용자 태그 삭제
    int deleteUserTagById(Long[] tagIds, Long UserId);

    //4. 게시글 태그 삭제
    int deletePostTagById(Long[] tagIds);



    //6.특정 게시글의 태그(들) 가져오기
    List<Tag> FindTagByPostId(Long postId);

    //7.특정 사용자의 태그(들) 가져오기
    List<Tag> FindTagByUserId(Long userId);

}