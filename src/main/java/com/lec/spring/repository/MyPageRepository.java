package com.lec.spring.repository;

import com.lec.spring.domain.ProfileUpdateForm;
import com.lec.spring.domain.User;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MyPageRepository {

    ProfileUpdateForm selectProfileUpdateForm(@Param("userId") Long userId);

    User selectUserById(@Param("userId") Long userId);

    List<Post> selectMyPosts(@Param("userId") Long userId);

    List<Comment> selectMyCommentsPaged(Map<String, Object> params);

    long countMyComments(Map<String, Object> params);

    List<User> selectMyFollowing(@Param("userId") Long userId);
    List<User> selectMyFollowingPaged(Map<String,Object> params);

    /** 내가 포스트에서 픽한 댓글이 있는 게시글 목록 (페이징) */
    List<Post> selectMyPickedCommentPostsPaged(Map<String, Object> params);

    /** 내가 포스트에서 픽한 댓글이 있는 게시글 개수 */
    long countMyPickedCommentPosts(@Param("userId") Long userId);


    long countMyFollowing(@Param("userId") Long userId);

    int updateUser(User user);

    int updateProfile(User user);

    List<Post> selectMyPostsPaged(Map<String, Object> params);

    long countMyPostsFiltered(Map<String, Object> params);

    int countMyPosts(long userId);

    void insertFollow(@Param("userId") Long userId,
                      @Param("followedUserId") Long followedUserId);

    void deleteFollow(@Param("userId") Long userId,
                      @Param("followedUserId") Long followedUserId);

}
