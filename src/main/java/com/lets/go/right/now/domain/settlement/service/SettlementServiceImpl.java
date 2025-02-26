package com.lets.go.right.now.domain.settlement.service;

import com.lets.go.right.now.domain.settlement.repository.SettlementResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {
    private final SettlementResultRepository settlementResultRepository;

    @Override
    public ResponseEntity<?> createPrepayment(Long tripId, String senderEmail) {
        return null;
    }
}
