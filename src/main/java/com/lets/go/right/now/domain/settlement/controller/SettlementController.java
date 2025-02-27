package com.lets.go.right.now.domain.settlement.controller;

import com.lets.go.right.now.domain.settlement.dto.PaymentCreateReq;
import com.lets.go.right.now.domain.settlement.service.SettlementService;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/settlement")
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;

    /**
     * 여행 지출 정산 하기
     */
    @PostMapping("{trip_id}")
    public ResponseEntity<?> calculateTravelSettlement(@PathVariable("trip_id") Long tripId) {
        return settlementService.calculateTravelSettlement(tripId);
    }

    /**
     * 미리 걷은 돈 기록하기
     */
    @PostMapping("{trip_id}/prepayment")
    public ResponseEntity<?> createPrepayment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("trip_id") Long tripId,
            @RequestBody PaymentCreateReq paymentCreateReq) {
        return settlementService.createPrepayment(paymentCreateReq, tripId, customUserDetails.getEmail());
    }

    /**
     * 송금 하기(송금 정보 저장 하기)
     */
    @PostMapping("{trip_id}/send")
    public ResponseEntity<?> sendTravelSettlement(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("trip_id") Long tripId,
            @RequestBody PaymentCreateReq paymentCreateReq) {
        return settlementService.sendTravelSettlement(
                paymentCreateReq, tripId, customUserDetails.getEmail());
    }
}
