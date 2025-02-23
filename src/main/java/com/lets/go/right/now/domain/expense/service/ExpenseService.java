package com.lets.go.right.now.domain.expense.service;

import com.lets.go.right.now.domain.expense.dto.ExpenseCreateReq;
import java.io.IOException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ExpenseService {
    ResponseEntity<?> createExpense(Long tripId, ExpenseCreateReq expenseCreateReq, List<MultipartFile> images)
            throws IOException;
}
