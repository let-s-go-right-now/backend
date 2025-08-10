package com.lets.go.right.now.domain.settlement.dto;

import com.lets.go.right.now.domain.expense.dto.MemberProfileViewRes;
import com.lets.go.right.now.domain.settlement.entity.TravelSettlement;
import com.lets.go.right.now.global.enums.Status;

/**
 * 여행 정산 결과에 대한 정보
 */
public record TravelSettlementResult(
        Long id, // trip_settlement_id
        MemberProfileViewRes sender, // 돈을 보낼 사람
        MemberProfileViewRes receiver, // 돈을 받을 사람
        Integer amount, // 보내야 할 돈
        Status settlementStatus
) {
    public static TravelSettlementResult of(TravelSettlement travelSettlement) {
        MemberProfileViewRes sender = MemberProfileViewRes.of(travelSettlement.getSender());
        MemberProfileViewRes receiver = MemberProfileViewRes.of(travelSettlement.getReceiver());
        return new TravelSettlementResult(
                travelSettlement.getId(),
                sender,
                receiver,
                travelSettlement.getAmount(),
                travelSettlement.getSettlementStatus()
        );
    }
}
