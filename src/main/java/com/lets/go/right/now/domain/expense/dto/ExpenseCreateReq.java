package com.lets.go.right.now.domain.expense.dto;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
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
    public static Expense of(ExpenseCreateReq expenseCreateReq, Trip trip, Member payer) {
        return Expense.builder()
                .expenseName(expenseCreateReq.expenseName())
                .price(expenseCreateReq.price())
                .details(expenseCreateReq.details())
                .expenseDate(expenseCreateReq.expenseDate())
                .trip(trip)
                .payer(payer)
                .build();
    }
}
