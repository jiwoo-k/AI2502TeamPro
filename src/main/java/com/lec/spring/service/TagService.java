package com.lec.spring.service;

import com.lec.spring.domain.Tag;
import com.lec.spring.repository.AttachmentRepository;
import com.lec.spring.repository.PostRepository;
import com.lec.spring.repository.TagRepository;
import com.lec.spring.repository.UserRepository;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;
    // 얘를 바로 주입받으려 하면 bean 으로 등록한 적이 없기 때문에 안됨
    //SqlSession Bean은 알아서 만들어주기 때문에 얘중에서 postrepository 를 가져오면 됨

    public TagService(SqlSession sqlSession) {
        tagRepository = sqlSession.getMapper(TagRepository.class);
        System.out.println("🎃TagService() 생성");
    }

    //1. 태그 추가
    public int add(Tag tag){
        return tagRepository.addTag(tag);
    }

    //2. 태그 검색하기
    public Tag findTag(String name, Long category_id){
        Tag tag = tagRepository.searchTag(name, category_id);
        return tag;
    }


}
