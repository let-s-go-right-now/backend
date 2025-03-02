package com.lets.go.right.now.domain.settlement.dto;

import com.lets.go.right.now.domain.expense.dto.MemberProfileViewRes;
import java.util.List;

public record TravelSettlementStatusRes(
        MemberProfileViewRes tripMemberProfile,
        List<TravelSettlementStatus> settlementStatuses // 정산 현황
) {
}
