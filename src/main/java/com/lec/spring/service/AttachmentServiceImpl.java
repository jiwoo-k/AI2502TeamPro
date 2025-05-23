package com.lec.spring.service;

import com.lec.spring.domain.Attachment;
import com.lec.spring.repository.AttachmentRepository;
import com.lec.spring.util.U;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class AttachmentServiceImpl implements AttachmentService {


    @Value("${app.upload.path}")
    private String uploadDir;

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
    public List<Attachment> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return attachmentRepository.findByIds(ids);
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return 0;
        return attachmentRepository.deleteByIds(ids);
    }

    @Override
    public int delete(Attachment file) {
        if (file == null || file.getId() == null) return 0;
        return attachmentRepository.delete(file);
    }


    // 특정 글(id)  에 첨부파일(들) (files) 추가
    @Override
    public void addFiles(Map<String, MultipartFile> files, Long id) {
        if (files == null) return;

        for (Map.Entry<String, MultipartFile> e : files.entrySet()) {
            // name="upfile##" 인 경우만 첨부파일 등록. (이유, 다른 웹에디터와 섞이지 않도록... ex: summernote)
            if (!e.getKey().startsWith("upfile")) continue;

            // 첨부파일 정보 출력
            System.out.println("\n첨부파일 정보: " + e.getKey());    // name = 값
            U.printFileInfo(e.getValue());   // MultipartFile 정보
            System.out.println();

            // 물리적인 파일 저장
            Attachment file = upload(e.getValue());

            // 성공하면 DB 에도 저장
            if (file != null) {
                file.setPostId(id);   // FK 설정
                attachmentRepository.save(file);   // INSERT
            }

        }
    }

    // 물리적으로 서버에 파일 저장.  중복된 파일 이름 -> rename 처리.
    @Override
    public Attachment upload(MultipartFile multipartFile) {
        Attachment attachment = null;

        // 담긴 파일이 없으면 pass
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) return null;

        // 원본 파일명
        String sourceName = StringUtils.cleanPath(originalFilename);

        // 현재 시각 timestamp
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"));

        // 확장자 분리
        String fileName;
        int pos = sourceName.lastIndexOf(".");
        if (pos > -1) {
            String name = sourceName.substring(0, pos);
            String ext = sourceName.substring(pos); // includes the dot
            fileName = name + "-" + timestamp + ext;
        } else {
            fileName = sourceName + "-" + timestamp;
        }

        System.out.println("\tfileName = " + fileName);

        Path copyOfLocation = Paths.get(new File(uploadDir, fileName).getAbsolutePath());
        System.out.println("\t" + copyOfLocation);

        try {
            Files.copy(
                    multipartFile.getInputStream(),
                    copyOfLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String contentType = multipartFile.getContentType();
        boolean isImage = contentType != null && contentType.startsWith("image");

        attachment = Attachment.builder()
                .fileName(fileName)         // 저장된 이름
                .sourceName(sourceName)     // 원본 이름
                .isImage(isImage)
                .build();

        return attachment;
    }


    // 특정 첨부파일을 물리적으로 삭제
    @Override
    public void delFiles(Attachment file) {
        String saveDirectory = new File(uploadDir).getAbsolutePath();

        File f = new File(saveDirectory, file.getFileName());
        System.out.println("삭제시도 -->" + f.getAbsolutePath());
        if (f.exists()) {
            if (f.delete())
                System.out.println("삭제 성공");
            else
                System.out.println("삭제 실패");
        } else {
            System.out.println("파일이 존재하지 않습니다.");
        }
    }

    // [이미지 파일 여부 세팅]
    @Override
    public void setImage(List<Attachment> fileList) {
        // upload 실제 물리적인 경로
        String realPath = new File(uploadDir).getAbsolutePath();

        for (Attachment attachment : fileList) {
            BufferedImage imgData = null;
            File f = new File(realPath, attachment.getFileName());  // 저장된 첨부파일에 대한 File 객체
            try {
                imgData = ImageIO.read(f);
                // ※ ↑ 파일이 존재 하지 않으면 IOExcepion 발생한다
                //   ↑ 이미지가 아닌 경우는 null 리턴
            } catch (IOException e) {
                System.out.println("No File : " + f.getAbsolutePath() + " [" + e.getMessage() + "]");
            }

            if (imgData != null) attachment.setImage(true);   // 이미지 여부 체크
        }

    }


}
