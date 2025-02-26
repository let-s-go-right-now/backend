package com.lets.go.right.now.domain.settlement.controller;

import com.lets.go.right.now.domain.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/settlement")
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;
}
