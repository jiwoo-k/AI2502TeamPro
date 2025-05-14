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
    public int insert(List<Map<String, Object>> list, Long PostId) {
        return 0;
    }

    @Override
    public int save(Attachment file) {
        return 0;
    }

    @Override
    public List<Attachment> findByPost(Long postId) {
        return List.of();
    }

    @Override
    public Attachment findById(Long id) {
        return attachmentRepository.findById(id);
    }

    @Override
    public List<Attachment> findByIds(Long[] ids) {
        return List.of();
    }

    @Override
    public int deleteByIds(Long[] ids) {
        return 0;
    }

    @Override
    public int delete(Attachment file) {
        return 0;
    }
}
