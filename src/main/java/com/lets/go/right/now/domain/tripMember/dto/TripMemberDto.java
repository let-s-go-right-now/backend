package com.lets.go.right.now.domain.tripMember.dto;

import com.lets.go.right.now.domain.tripMember.enums.Status;
import lombok.Data;

@Data
public class TripMemberDto {
    private Long memberId;
    private String name;
    private String email;
    private Status settlementStatus; // enum 타입 (READY, COMPLETE 등)
}
