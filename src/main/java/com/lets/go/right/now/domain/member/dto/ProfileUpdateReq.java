package com.lets.go.right.now.domain.member.dto;

import org.springframework.web.multipart.MultipartFile;

public record ProfileUpdateReq (
        String name,               // 수정할 이름
        String accountNumber,      // 수정할 계좌 번호
        MultipartFile profileImgLink // 수정할 프로필 이미지
) {}
