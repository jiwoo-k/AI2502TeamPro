//package com.lec.spring.service;
//
//import com.lec.spring.domain.User;
//import com.lec.spring.domain.UserFollowing;
//import com.lec.spring.repository.UserFollowingRepository;
//import org.apache.ibatis.session.SqlSession;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
//@Service
//public class UserFollowingServiceImpl implements UserFollowingService {
//
//    private final UserFollowingRepository repository;
//
//    /**
//     * 생성자에서 SqlSession 을 주입받고, 이를 통해 MyBatis Mapper 로부터
//     * UserFollowingRepository 구현체를 획득
//     */
//    public UserFollowingServiceImpl(SqlSession sqlSession) {
//        this.repository = sqlSession.getMapper(UserFollowingRepository.class);
//    }
//
//    @Override
//    public int follow(User followingUser, User followedUser) {
//        // 두 User 객체를 기반으로 UserFollowing 객체를 생성 (빌더 패턴 사용)
//        UserFollowing uf = UserFollowing.builder()
//                .followingUserId(followingUser.getId())
//                .followedUserId(followedUser.getId())
//                .followingUser(followingUser)
//                .followedUser(followedUser)
//                .build();
//        // Repository 를 통해 INSERT 작업 수행
//        return repository.insert(uf);
//    }
//
//    @Override
//    public int unfollow(User followingUser, User followedUser) {
//        // 언팔로우할 경우 ID 정보만으로 충분
//        UserFollowing uf = UserFollowing.builder()
//                .followingUserId(followingUser.getId())
//                .followedUserId(followedUser.getId())
//                .build();
//        // Repository 를 통해 DELETE 작업 수행
//        return repository.delete(uf);
//    }
//
//    @Override
//    public List<UserFollowing> getFollowingList(Long followingUserId) {
//        return repository.findByFollowingUserId(followingUserId);
//    }
//
//    @Override
//    public List<UserFollowing> getFollowersList(Long followedUserId) {
//        return repository.findByFollowedUserId(followedUserId);
//    }
//}
