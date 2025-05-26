package com.lec.spring.repository;

import com.lec.spring.domain.Post;
import com.lec.spring.domain.Tag;
import com.lec.spring.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface TagRepository {
    //1. 새로운 태그 추가하기
    int addTag(Tag tag);

    //2. 특정 태그가 존재한다면 불러오기
    Tag searchTag(Tag tag);

    //3. 담은 태그 목록에 해당하는 게시글(들) 불러오기
    List<Post> findPostsByTags(List<Tag> tags, String postType);


    List<Tag> findByIds(@Param("ids") List<Long> ids);

    //4. 새로운 게시글 태그(들) 저장
    void savePostTag(Map<String, Object> paramMap);

    //5. 새로운 사용자 태그 저장
    int saveUserTag(List<Tag> tags, Long UserId);

    //6. 사용자 태그 삭제
    int deleteUserTag(User user);

    //7. 게시글 태그 삭제
    int deletePostTag(Post post);

    //8. 게시글 id 로 특정 게시글의 태그(들) 가져오기
    List<Tag> loadPostTags(@Param("post_id") Long boardId);

    //9.사용자 id 로 특정 게시글의 태그(들) 가져오기
    List<Tag> loadUserTags(Long userId);

    Tag findById(Long id);


}