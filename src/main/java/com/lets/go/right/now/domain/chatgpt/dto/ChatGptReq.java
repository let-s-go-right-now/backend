package com.lets.go.right.now.domain.chatgpt.dto;

// 회원이 입력하는 데이터

public record ChatGptReq(
        String startDate,
        String endDate,
        int budget,
        String transportMode,
        String departure
) {}
