package com.lets.go.right.now.domain.chatgpt.dto;

// 회원이 입력하는 데이터

import java.time.LocalDate;

public record ChatGptReq(
        LocalDate startDate,
        LocalDate endDate,
        int budget,
        String transportMode,
        String departure
) {}
