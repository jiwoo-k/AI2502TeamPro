package com.lec.spring.service;

import com.lec.spring.domain.ProfileUpdateForm;
import com.lec.spring.domain.User;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.Comment;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MyPageService {
    User getUserById(Long userId);
    Page<Post> getMyPosts(Long userId, String selectedType, Pageable pageable);
    Page<Comment> getMyComments(Long userId, Pageable pageable);

    Page<Post> getMyPickedCommentPosts(Long userId, Pageable pageable);


    Page<User> getMyFollowing(Long userId, Pageable pageable);

    void updateUser(User user);
    void updateUserProfile(User user);
    ProfileUpdateForm getProfileUpdateForm(Long userId);

    void followUser(Long userId, Long followedUserId);
    void unfollowUser(Long userId, Long followedUserId);
}
