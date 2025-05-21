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
        int limit = pageable.getPageSize();

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("selectedType", selectedType);
        params.put("limit", limit);
        params.put("offset", offset);

        List<Post> posts = repo.selectMyPostsPaged(params);
        long totalCount = repo.countMyPostsFiltered(params);
        return new PageImpl<>(posts, pageable, totalCount);
    }

    // --- 내 댓글 페이징 추가 ---
    @Override
    public Page<Comment> getMyComments(Long userId, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", limit);
        params.put("offset", offset);

        List<Comment> comments = repo.selectMyCommentsPaged(params);
        long totalCnt = repo.countMyComments(params);
        return new PageImpl<>(comments, pageable, totalCnt);
    }

    // service 구현부
    @Override
    public Page<User> getMyFollowing(Long userId, Pageable pageable) {
        // 1) 파라미터 맵 구성: userId, limit, offset
        Map<String,Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit",  pageable.getPageSize());
        params.put("offset", (int)pageable.getOffset());  // MyBatis 매퍼의 #{offset}이 int 타입이라면 캐스팅 필요

        // 2) 호출
        List<User> list = repo.selectMyFollowingPaged(params);
        long total    = repo.countMyFollowing(userId);

        // 3) PageImpl 생성
        return new PageImpl<>(list, pageable, total);
    }


    @Override
    public void updateUser(User user) {
        repo.updateUser(user);
    }

    @Override
    public void updateUserProfile(User user) {
        repo.updateProfile(user);
    }

    @Override
    public void followUser(Long userId, Long followedUserId) {
        repo.insertFollow(userId, followedUserId);
    }

    @Override
    public void unfollowUser(Long userId, Long followedUserId) {
        repo.deleteFollow(userId, followedUserId);
    }
}
