package com.lets.go.right.now.domain.settlement.dto;

import com.lets.go.right.now.domain.settlement.dto.enums.Status;
import java.util.List;

public record TravelSettlementStatus(
        Status status, // RECEIVED, SEND
        Integer amount,
        List<String> relatedMemberName // 해당 지출에 연관된 회원의 이름
) {
}
