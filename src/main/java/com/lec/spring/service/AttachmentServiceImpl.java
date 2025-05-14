package com.lec.spring.service;

import com.lec.spring.domain.Attachment;
import com.lec.spring.repository.AttachmentRepository;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl(SqlSession sqlSession) {
        this.attachmentRepository = sqlSession.getMapper(AttachmentRepository.class);
    }

    @Override
    public int insert(List<Map<String, Object>> list, Long postId) {
        if (list == null || list.isEmpty() || postId == null) return 0;
        return attachmentRepository.insert(list, postId);
    }

    @Override
    public int save(Attachment file) {
        if (file == null) return 0;
        return attachmentRepository.save(file);
    }

    @Override
    public List<Attachment> findByPost(Long postId) {
        if (postId == null) return List.of();
        return attachmentRepository.findByPost(postId);
    }

    @Override
    public Attachment findById(Long id) {
        if (id == null) return null;
        return attachmentRepository.findById(id);
    }

    @Override
    public List<Attachment> findByIds(Long[] ids) {
        if (ids == null || ids.length == 0) return List.of();
        return attachmentRepository.findByIds(ids);
    }

    @Override
    public int deleteByIds(Long[] ids) {
        if (ids == null || ids.length == 0) return 0;
        return attachmentRepository.deleteByIds(ids);
    }

    @Override
    public int delete(Attachment file) {
        if (file == null || file.getId() == null) return 0;
        return attachmentRepository.delete(file);
    }
}
