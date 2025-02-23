package com.lets.go.right.now.domain.expense.dto;

import com.lets.go.right.now.domain.expense.entity.Category;
import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.member.entity.Member;
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
    public static Expense of(ExpenseCreateReq expenseCreateReq) {
        return Expense.builder()
                .expenseName(expenseCreateReq.expenseName())
                .price(expenseCreateReq.price())
                .details(expenseCreateReq.details())
                .expenseDate(expenseCreateReq.expenseDate())
                // 카테고리 추가 필요
                .build();
    }
}
