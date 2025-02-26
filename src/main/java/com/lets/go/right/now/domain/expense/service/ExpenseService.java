package com.lets.go.right.now.domain.expense.service;

import com.lets.go.right.now.domain.expense.dto.ExpenseCreateReq;
import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ExpenseService {
    ResponseEntity<?> createExpense(Long tripId, ExpenseCreateReq expenseCreateReq, List<MultipartFile> images)
            throws IOException;

    ResponseEntity<?> deleteExpense(Long expenseId)
            throws IOException;

    ResponseEntity<?> getExpenseInfo(Long expenseId);

    ResponseEntity<?> editExpense(Long expenseId, ExpenseCreateReq expenseCreateReq, List<MultipartFile> images)
            throws IOException;

    ResponseEntity<?> getMyExpenses(Long tripId, String email, int page, int size);
}
