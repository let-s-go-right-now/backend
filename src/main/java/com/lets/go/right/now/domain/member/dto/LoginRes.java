package com.lets.go.right.now.domain.member.dto;

import com.lets.go.right.now.domain.member.entity.Member;

public record LoginRes(
        String name,
        String profileImgLink,
        String accountName,
        String accessToken
) {
    public static LoginRes of(Member member, String accessToken) {
        return new LoginRes(
                member.getName(),
                member.getProfileImgLink(),
                member.getAccountNumber(),
                accessToken);
    }
}
