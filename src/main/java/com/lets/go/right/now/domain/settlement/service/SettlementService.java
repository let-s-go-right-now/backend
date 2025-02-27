package com.lets.go.right.now.domain.settlement.service;

import com.lets.go.right.now.domain.settlement.dto.PaymentCreateReq;
import org.springframework.http.ResponseEntity;

public interface SettlementService {
    ResponseEntity<?> createPrepayment(
            PaymentCreateReq paymentCreateReq, Long tripId, String senderEmail);

    ResponseEntity<?> sendTravelSettlement(Long travelSettlementId);

    ResponseEntity<?> calculateTravelSettlement(Long tripId);
}
