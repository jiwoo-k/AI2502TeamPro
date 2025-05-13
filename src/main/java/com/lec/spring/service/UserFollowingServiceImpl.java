package com.lec.spring.service;

import com.lec.spring.domain.User;
import com.lec.spring.domain.UserFollowing;
import com.lec.spring.repository.UserFollowingRepository;
// import org.apache.ibatis.session.SqlSession; // 더 이상 SqlSession을 직접 주입받지 않으므로 이 import는 제거합니다.
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 관리를 위한 import를 추가합니다.
import java.util.List;

@Service
public class UserFollowingServiceImpl implements UserFollowingService {

    private final UserFollowingRepository repository;


    public UserFollowingServiceImpl(UserFollowingRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional // **[추가됨]** 팔로우 작업은 데이터베이스에 쓰기(INSERT) 작업을 포함하므로 트랜잭션 관리가 필요
    public int follow(User followingUser, User followedUser) {
        // (추가 고려 사항)
        // 1. 팔로우 대상이 본인인지 확인하는 로직을 추가할 수 있습니다.
        // if (followingUser.getId().equals(followedUser.getId())) {
        //    // 자신을 팔로우할 수 없음
        //    return 0; // 또는 throw new IllegalArgumentException("자신을 팔로우할 수 없습니다.");
        // }

        // 2. 이미 팔로우 중인지 확인하는 로직을 추가할 수 있습니다. (Repository 에 해당 메소드 필요)
        // if (repository.countByFollowingUserIdAndFollowedUserId(followingUser.getId(), followedUser.getId()) > 0) {
        //    // 이미 팔로우 중임
        //    return 0; // 또는 throw new IllegalStateException("이미 팔로우 중입니다.");
        // }

        // 두 User 객체의 ID를 기반으로 UserFollowing 객체를 생성 (빌더 패턴 사용)
        UserFollowing uf = UserFollowing.builder()
                .followingUserId(followingUser.getId()) // ID만 전달
                .followedUserId(followedUser.getId())   // ID만 전달
                .build();
        // Repository 를 통해 INSERT 작업 수행
        return repository.insert(uf);
    }

    @Override
    @Transactional // 언팔로우 작업도 데이터베이스에 쓰기(DELETE) 작업을 포함하므로 트랜잭션 관리가 필요
    public int unfollow(User followingUser, User followedUser) {

        // 언팔로우할 경우 ID 정보만으로 충분
        UserFollowing uf = UserFollowing.builder()
                .followingUserId(followingUser.getId())
                .followedUserId(followedUser.getId())
                .build();
        // Repository 를 통해 DELETE 작업 수행
        return repository.delete(uf);
    }

    @Override
    @Transactional(readOnly = true) // 조회 작업은 읽기 전용 트랜잭션으로 설정하여 성능을 최적화합니다.
    public List<UserFollowing> getFollowingList(Long followingUserId) {
        return repository.findByFollowingUserId(followingUserId);
    }

    @Override
    @Transactional(readOnly = true) // 조회 작업은 읽기 전용 트랜잭션으로 설정하여 성능을 최적화합니다.
    public List<UserFollowing> getFollowersList(Long followedUserId) {
        return repository.findByFollowedUserId(followedUserId);
    }
}