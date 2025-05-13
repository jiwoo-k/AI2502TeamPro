package com.lec.spring.repository;

import com.lec.spring.domain.Authority;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthorityRepository {

    /**
     * 특정 ID의 권한 정보를 조회
     */
    Authority findById(Long id);

    /**
     * 특정 이름의 권한 정보를 조회
     * Spring Security 설정 등에서 권한 이름을 기반으로 Authority 객체를 찾을 때 유용
     */
    Authority findByName(String name);

    /**
     * 모든 권한 목록을 조회
     */
    List<Authority> findAll();

}