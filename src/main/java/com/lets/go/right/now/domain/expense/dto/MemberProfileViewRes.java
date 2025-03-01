package com.lets.go.right.now.domain.expense.dto;

import com.lets.go.right.now.domain.member.entity.Member;

/**
 * 회원 프로필 DTO
 * 이름, 이메일, 프로필 이미지
 */
public record MemberProfileViewRes(
        String name,
        String email,
        String profileImageUrl
) {
    public static MemberProfileViewRes of(Member member) {
        return new MemberProfileViewRes(member.getName(), member.getEmail(), member.getProfileImgLink());
    }
}
