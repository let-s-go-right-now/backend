package com.lets.go.right.now.domain.settlement.dto;

import java.util.List;

public record TravelSettlementResultRes(
        Integer totalAmount,
        List<TravelSettlementResult> settlementResults
) {
    public static TravelSettlementResultRes of(Integer totalAmount, List<TravelSettlementResult> settlementResults) {
        return new TravelSettlementResultRes(totalAmount, settlementResults);
    }
}
