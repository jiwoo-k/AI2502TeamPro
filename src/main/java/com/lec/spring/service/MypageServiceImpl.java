// src/main/java/com/lec/spring/service/MypageServiceImpl.java
package com.lec.spring.service;

import com.lec.spring.domain.ProfileUpdateForm;
import com.lec.spring.domain.User;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.Comment;
import com.lec.spring.repository.MypageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MypageServiceImpl implements MypageService {

    private final MypageRepository repo;

    public MypageServiceImpl(MypageRepository repo) {
        this.repo = repo;
    }

    @Override
    public User getUserById(Long userId) {
        return repo.selectUserById(userId);
    }

    @Override
    public ProfileUpdateForm getProfileUpdateForm(Long userId) {
        return repo.selectProfileUpdateForm(userId);
    }

    // --- 내 게시글 페이징 + type 필터링 추가 ---
    @Override
    public Page<Post> getMyPosts(Long userId, String selectedType, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit  = pageable.getPageSize();

        Map<String,Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("selectedType", selectedType);
        params.put("pageSize", limit);
        params.put("offset", offset);

        List<Post> posts     = repo.selectMyPostsPaged(params);
        long      totalCount = repo.countMyPostsFiltered(params);
        return new PageImpl<>(posts, pageable, totalCount);
    }

    // --- 내 댓글 페이징 추가 ---
    @Override
    public Page<Comment> getMyComments(Long userId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit  = pageable.getPageSize();

        Map<String,Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("pageSize", limit);
        params.put("offset", offset);

        List<Comment> comments  = repo.selectMyCommentsPaged(params);
        long           totalCnt = repo.countMyComments(params);
        return new PageImpl<>(comments, pageable, totalCnt);
    }

    @Override
    public List<User> getMyFollowing(Long userId) {
        return repo.selectMyFollowing(userId);
    }

    @Override
    public void updateUser(User user) {
        repo.updateUser(user);
    }

    @Override
    public void updateUserProfile(User user) {
        repo.updateProfile(user);
    }
}
