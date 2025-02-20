package com.lets.go.right.now.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * 회원 가입에 사용
 */
@Data
@Builder
public class JoinDTO {
    @NotNull(message = "회원 이름은 필수입니다.")
    private String name;
    @Email(message = "이메일 형식을 지켜주세요")
    @NotNull(message = "이메일은 필수입니다.")
    private String email;
    @NotNull(message = "비밀번호는 필수입니다.")
    private String password;
    @NotNull(message = "계좌 번호는 필수입니다.")
    private String accountNumber; // 계좌 번호
}
