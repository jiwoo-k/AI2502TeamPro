package com.lec.spring.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfileUpdateForm {
    @NotBlank(message = "닉네임은 반드시 입력해야 합니다.")
    private String name;

//    @Size(min = 8, message = "새 비밀번호는 8자 이상이어야 합니다.")
    private String newPassword;

//    @NotBlank(message = "비밀번호 확인을 입력하세요.")
    private String confirmPassword;

    // 화면에 콤마로 전달된 태그 문자열
    private String tags;

    // getter / setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
}
