package com.lec.spring.service;

import com.lec.spring.domain.Tag;
import com.lec.spring.repository.TagRepository;
import com.lec.spring.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Tag findById(Long id) {
        return tagRepository.findById(id);
    }

    @Override
    public List<Tag> findTagsByIds(List<Long> ids) {
        return tagRepository.findByIds(ids);
    }

    @Override
    public List<Tag> findTagsByBoardId(Long boardId) {
        return tagRepository.loadPostTags(boardId);
    }
}
