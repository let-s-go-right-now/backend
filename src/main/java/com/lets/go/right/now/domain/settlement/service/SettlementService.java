package com.lets.go.right.now.domain.settlement.service;

import org.springframework.http.ResponseEntity;

public interface SettlementService {
    ResponseEntity<?> createPrepayment(Long tripId, String senderEmail);
}
