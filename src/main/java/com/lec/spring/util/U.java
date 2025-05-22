package com.lec.spring.util;
import com.lec.spring.config.PrincipalDetails;
import com.lec.spring.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

// 유틸리티 클래스: 다양한 공통 기능을 제공하는 static 메소드 모음
public class U {

    // 이 클래스는 유틸리티 클래스이므로 인스턴스 생성을 막는 private 생성자를 가짐
    private U() {}

    // 현재 HttpServletRequest 객체 가져오기
    // Spring MVC 환경에서 현재 스레드의 HttpServletRequest 객체에 접근
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attrs.getRequest();
    }

    // 현재 HttpSession 객체 가져오기
    // 현재 요청의 세션 객체를 가져옵니다.
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    // 현재 로그인 한 사용자 User 객체 가져오기
    public static User getLoggedUser(){
        // 현재 로그인 한 사용자
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        // 2. 익명 사용자 토큰인 경우
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        // 3. Principal이 UserDetails의 인스턴스인 경우
        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        if (userDetails != null) {
            return userDetails.getUser();
        }

        // 그 외 예상치 못한 경우 (예: Principal이 String 타입의 "anonymousUser"인 경우 등)
        return null;
    }


    // 현재 로그인 한 사용자 ID (Long) 가져오기
    public static Long getUserId() {
        User loggedUser = getLoggedUser();
        if (loggedUser != null) {
            return loggedUser.getId();
        }
        // 로그인되지 않았거나 User 객체에서 ID를 가져올 수 없는 경우
        return null;
    }


    // 첨부파일 정보(MultipartFile) 출력하기
    // 업로드된 파일의 정보 (이름, 크기, MIME 타입 등)를 콘솔에 출력하고, 이미지 파일인 경우 크기도 추가 출력합니다.
    public static void printFileInfo(MultipartFile file) {
        // 파일의 원본 이름 가져오기
        String originalFilename = file.getOriginalFilename();

        // 파일이 없거나 이름이 비어있는 경우 처리
        if (originalFilename == null || originalFilename.isEmpty()) {
            System.out.println("\t파일이 없습니다");
            return;
        }

        // 파일 정보 출력
        System.out.println("""
                    Original File Name : %s
                    CleanPath: %s
                    File Size : %s
                    MIME : %s
                """.formatted(
                originalFilename,
                // 파일 경로 정리 (운영체제별 경로 구분자 \ 를 / 로 변경 등)
                StringUtils.cleanPath(originalFilename),
                file.getSize() + "bytes",  // 용량 (byte 단위)
                file.getContentType()  // Content Type (MIME 타입)
        ));

        // 이미지 파일인 경우 추가 정보 (가로/세로 크기) 출력 시도
        try {
            // MultipartFile 의 InputStream 으로부터 BufferedImage 객체를 읽어옵니다.
            // ImageIO.read()는 이미지 파일이 아니거나 읽기 오류 시 null을 반환할 수 있습니다.
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            // BufferedImage 객체가 null이 아니면 이미지 파일로 성공적으로 읽었다는 의미
            if (bufferedImage != null) {
                System.out.printf("\t이미지 파일입니다: %d x %d\n", bufferedImage.getWidth(), bufferedImage.getHeight());
            } else {
                System.out.println("\t이미지 파일이 아닙니다");
            }

        } catch (IOException e) {
            // 이미지 파일 읽기 중 입출력 오류 발생 시
            System.err.println("\tError reading image file info (IO Exception): " + e.getMessage()); // 오류 메시지 출력
            // throw new RuntimeException(e); // 필요시 예외를 다시 발생시켜 상위에서 처리하도록 할 수 있습니다.
        } catch (Exception e) {
            // 이미지 처리 중 예상치 못한 다른 종류의 예외 발생 시
            System.err.println("\tAn unexpected error occurred while processing file info: " + e.getMessage());
            // throw new RuntimeException(e); // 필요시 예외 다시 발생
        }
    }

}