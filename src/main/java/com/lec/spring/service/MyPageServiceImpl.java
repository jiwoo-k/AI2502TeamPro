// src/main/java/com/lec/spring/service/MyPageServiceImpl.java
package com.lec.spring.service;

import com.lec.spring.domain.ProfileUpdateForm;
import com.lec.spring.domain.User;
import com.lec.spring.domain.Post;
import com.lec.spring.domain.Comment;
import com.lec.spring.repository.MyPageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MyPageServiceImpl implements MyPageService {

    private final PasswordEncoder passwordEncoder;

    private final MyPageRepository repo;
    // ① 로거 선언
    private static final Logger logger = LoggerFactory.getLogger(MyPageServiceImpl.class);


    public MyPageServiceImpl(MyPageRepository repo, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("limit", pageable.getPageSize());
        params.put("offset", (int) pageable.getOffset());  // MyBatis 매퍼의 #{offset}이 int 타입이라면 캐스팅 필요

        // 2) 호출
        List<User> list = repo.selectMyFollowingPaged(params);
        long total = repo.countMyFollowing(userId);

        // 3) PageImpl 생성
        return new PageImpl<>(list, pageable, total);
    }


    @Override
    public void updateUser(User user) {
        repo.updateUser(user);
    }

    @Override
    @Transactional
    public void updateUserProfile(User user) {
        // ② repo.updateUser(...)가 영향을 준 행 수 받아오기
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        logger.info("▶▶ updateUserProfile 호출 → id={}, 새닉네임='{}', 새비밀번호='{}'",
                user.getId(), user.getName(), user.getPassword());
        int updatedCount = repo.updateUser(user);
        logger.info("▶▶ updateUserProfile 결과 → 업데이트 행 수 = {}", updatedCount);
        if (updatedCount == 0) {
            throw new RuntimeException("회원정보 수정 실패: 업데이트된 행이 없습니다.");
        }
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
