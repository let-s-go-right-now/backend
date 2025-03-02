package com.lets.go.right.now.domain.expense.dto;

/**
 * 카테고리별 지출 리포트 DTO
 */
public record CategoryExpenseRes(
        String categoryName, // 카테고리 이름 (예: 교통, 숙박 등)
        double percentage,   // 총 지출 대비 비율 (예: 30%)
        Integer totalAmount  // 해당 카테고리의 총 지출액
) {
    public static CategoryExpenseRes of(String categoryName, double percentage, Integer totalAmount) {
        return new CategoryExpenseRes(categoryName, percentage, totalAmount);
    }
}
