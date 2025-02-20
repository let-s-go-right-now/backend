package com.lets.go.right.now.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * 회원 가입에 사용
 */
public record JoinReq(
    @NotNull(message = "회원 이름은 필수입니다.")
    String name,
    @Email(message = "이메일 형식을 지켜주세요")
    @NotNull(message = "이메일은 필수입니다.")
    String email,
    @NotNull(message = "비밀번호는 필수입니다.")
    String password,
    @NotNull(message = "계좌 번호는 필수입니다.")
    String accountNumber // 계좌 번호
){

}
