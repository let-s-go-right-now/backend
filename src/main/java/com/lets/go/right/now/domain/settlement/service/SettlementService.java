package com.lets.go.right.now.domain.settlement.service;

import com.lets.go.right.now.domain.settlement.dto.PrepaymentCreateReq;
import org.springframework.http.ResponseEntity;

public interface SettlementService {
    ResponseEntity<?> createPrepayment(
            PrepaymentCreateReq prepaymentCreateReq, Long tripId, String senderEmail);
}
