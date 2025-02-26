package com.lets.go.right.now.domain.expense.controller;

import com.lets.go.right.now.domain.expense.dto.ExpenseCreateReq;
import com.lets.go.right.now.domain.expense.service.ExpenseService;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expense")
public class ExpenseController {
    private final ExpenseService expenseService;

    /**
     * 지출 생성
     */
    @PostMapping("{trip_id}")
    public ResponseEntity<?> createExpense(
            @PathVariable("trip_id") Long tripId,
            @ModelAttribute ExpenseCreateReq expenseCreateReq,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {
        return expenseService.createExpense(tripId, expenseCreateReq, images);
    }

    /**
     * 지출 삭제
     */
    @DeleteMapping("{expense_id}")
    public ResponseEntity<?> deleteExpense(
            @PathVariable("expense_id") Long expenseId) throws IOException {
        return expenseService.deleteExpense(expenseId);
    }

    /**
     * 지출 정보 보기
     */
    @GetMapping("{expense_id}")
    public ResponseEntity<?> getExpenseInfo(
            @PathVariable("expense_id") Long expenseId){
        return expenseService.getExpenseInfo(expenseId);
    }

    /**
     * 지출 수정
     */
    @PutMapping("{expense_id}")
    public ResponseEntity<?> editExpense(
            @PathVariable("expense_id") Long expenseId,
            @ModelAttribute ExpenseCreateReq expenseCreateReq,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) throws IOException {
        return expenseService.editExpense(expenseId, expenseCreateReq, images);
    }

    /**
     * 내가 포함된 지출 보기
     */
    @GetMapping("{trip_id}/mine")
    public ResponseEntity<?> getMyExpenses(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("{trip_id}") Long tripId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size) {
        return expenseService.getMyExpenses(tripId, customUserDetails.getEmail(), page, size);
    }
}
