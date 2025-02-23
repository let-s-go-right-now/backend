package com.lets.go.right.now.domain.expense.dto;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.enums.Category;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import java.time.LocalDate;
import java.util.List;

public record ExpenseCreateReq(
        String expenseName, // 지출 이름
        Integer price, // 지출 금액
        String details, // 상세 내역
        LocalDate expenseDate, // 지출 날짜
        String categoryName, // Enum 타입으로 변환할 문자열
        String payerEmail,
        List<String> excludedMember
) {
    public static Expense of(ExpenseCreateReq expenseCreateReq, Trip trip, Member payer) {
        // 입력된 카테고리 문자열을 Enum으로 변환
        Category category = convertToCategory(expenseCreateReq.categoryName());

        return Expense.builder()
                .expenseName(expenseCreateReq.expenseName())
                .price(expenseCreateReq.price())
                .details(expenseCreateReq.details())
                .expenseDate(expenseCreateReq.expenseDate())
                .category(category)
                .trip(trip)
                .payer(payer)
                .build();
    }

    private static Category convertToCategory(String categoryName) {
        try {
            return Category.valueOf(categoryName.toUpperCase()); // 문자열을 ENUM으로 변환
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new GeneralException(ErrorStatus._CATEGORY_NOT_FOUND);
        }
    }
}
