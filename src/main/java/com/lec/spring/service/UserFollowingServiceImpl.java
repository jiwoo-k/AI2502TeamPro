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
        if (followingUser == null || followedUser == null) {
            System.out.println("follow 메서드 호출 시 사용자 객체가 null입니다.");
            return 0;
        }

        // (추가 고려 사항)
        // 1. 자기 자신 팔로우 X
        if (followedUser.getId().equals(followingUser.getId())) {
            return 0;
        }

        // 2. 이미 팔로우 중이면 X
        if (repository.findByFollow(followingUser.getId(), followedUser.getId()) != null) {
            return 0;
        }

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
        return repository.delete(uf.getFollowingUserId(), uf.getFollowedUserId());
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

    @Override
    public Boolean isFollowing(Long followingUserId, Long followedUserId) {
        System.out.println("👀 followingUserId = " + followingUserId);
        System.out.println("👀 followedUserId  = " + followedUserId);

        return repository
                .findByFollow(followingUserId, followedUserId) !=null;
    }

    @Override
    public int followCount(Long followingUserId) {
        Integer count = repository.followCount(followingUserId);
        return count != null ? count : 0;
    }
}