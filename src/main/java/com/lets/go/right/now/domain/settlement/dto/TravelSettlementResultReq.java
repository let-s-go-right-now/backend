package com.lets.go.right.now.domain.settlement.dto;

import java.util.List;

public record TravelSettlementResultReq(
        Integer totalAmount,
        List<TravelSettlementResult> settlementResults
) {
    public static TravelSettlementResultReq of(Integer totalAmount, List<TravelSettlementResult> settlementResults) {
        return new TravelSettlementResultReq(totalAmount, settlementResults);
    }
}
