package com.lets.go.right.now.domain.settlement.dto;

/**
 * 미리 정산한 금액 기록용
 * 누구에게 얼마를 보냈는지
 */
public record PaymentCreateReq(
        String receiverEmail,
        Integer amount
) {
}
