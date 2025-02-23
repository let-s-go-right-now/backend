package com.lets.go.right.now.domain.expense.controller;

import com.lets.go.right.now.domain.expense.dto.ExpenseCreateReq;
import com.lets.go.right.now.domain.expense.service.ExpenseService;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping("{trip_id}")
    public ResponseEntity<?> createExpense(
            @PathVariable("trip_id") Long tripId,
            @ModelAttribute ExpenseCreateReq expenseCreateReq,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        return expenseService.createExpense(tripId, expenseCreateReq, images);
    }
}
