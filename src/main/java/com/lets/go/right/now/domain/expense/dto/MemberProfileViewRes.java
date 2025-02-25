package com.lets.go.right.now.domain.expense.dto;

/**
 * 회원 프로필 DTO
 * 이름, 이메일, 프로필 이미지
 */
public record MemberProfileViewRes(
        String name,
        String email,
        String profileImageUrl
) {
}
