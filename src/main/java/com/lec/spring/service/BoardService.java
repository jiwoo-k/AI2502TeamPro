package com.lec.spring.service;

import com.lec.spring.domain.Post;
import com.lec.spring.domain.Tag;
import com.lec.spring.domain.User;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface BoardService {
    // 게시판 작성하기
    int write(Post Post);

    int write(Post post, Map<String, MultipartFile> files);


    // id 가져와서 글 읽기
    Post detail(Long id);

    // 게시판 목록
    List<Post> list();

    // 페이징
    List<Post> list(Integer page, Model model);

    // 게시판 수정하기
    int update(Post Post);

    int update(Post post, Map<String, MultipartFile> files, Long[] delfile);


    // 게시판 삭제
    int delete(Long id);

    // 타입 선택 기능 추가
    List<Post> listByType(String type);

    //타입 + 위치정보
    List<Post> listByTypeLocation(String type, List<User> users);

    // 삭제 정보 저장
    void deleteTime(Long id);

    public List<Tag> postTagList(Long post_id);


}
