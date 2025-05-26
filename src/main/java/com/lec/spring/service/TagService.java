package com.lec.spring.service;

import com.lec.spring.domain.Tag;

import java.util.List;

public interface TagService {
    Tag findById(Long id);
    List<Tag> findTagsByIds(List<Long> ids);

    List<Tag> findTagsByBoardId (Long boardId);
}
