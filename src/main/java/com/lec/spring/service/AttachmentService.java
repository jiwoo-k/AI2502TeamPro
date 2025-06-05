package com.lec.spring.service;

import com.lec.spring.domain.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface AttachmentService {

    int insert(List<Map<String, Object>> list, Long PostId);


    // 첨부파일 한개 저장 INSERT
    int save(Attachment file);

    // 특정 글(postId) 의 첨부파일들 SELECT
    List<Attachment> findByPost(Long postId);


    // 특정 첨부파일(id) 한개 SELECT
    Attachment findById(Long id);


    // 선택된 첨부파일들 SELECT
    // 글 수정 에서 사용
    List<Attachment> findByIds(List<Long> ids);


    // 선택된 첨부파일들 DELETE
    // 글 수정 에서 사용
    int deleteByIds(List<Long> ids);


    // 특정 첨부 파일(file)을 DB에서 DELETE
    int delete(Attachment file);

    void addFiles(Map<String, MultipartFile> files, Long id);

    Attachment upload(MultipartFile file);

    void delFiles (Attachment file);

    void setImage(List<Attachment> fileList);

}
