package com.lets.go.right.now.domain.expense.dto;

import java.time.LocalDate;
import java.util.List;

public record ExpenseCreateReq(
        String expenseName, // 지출 이름
        Integer price, // 지출 금액
        String details, // 상세 내역
        LocalDate expenseDate, // 지출 날짜
        String categoryName,
        String payerEmail,
        List<String> excludedMember
) {
}
