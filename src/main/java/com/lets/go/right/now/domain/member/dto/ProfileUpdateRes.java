package com.lets.go.right.now.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)  // null 값인 필드 제외 출력
public record ProfileUpdateRes(
        String name,             // 수정된 이름
        String accountNumber,    // 수정된 계좌 번호
        String profileImgLink    // 수정된 프로필 이미지 URL
) {

    // 계좌 번호만 수정된 경우
    public static ProfileUpdateRes ofAccountNumber(String accountNumber) {
        return new ProfileUpdateRes(null, accountNumber, null);
    }

    // 프로필 이미지만 수정된 경우
    public static ProfileUpdateRes ofProfileImgLink(String profileImgLink) {
        return new ProfileUpdateRes(null, null, profileImgLink);
    }
}
