package com.lets.go.right.now.domain.expense.dto;

/**
 * 날짜별 여행 총 지출 DTO
 */
public record DailyExpenseRes(
        int day, // 1일차, 2일차, ...
        Integer totalAmount // 해당 날짜의 총 지출액
) {
    public static DailyExpenseRes of(int day, Integer totalAmount) {
        return new DailyExpenseRes(day, totalAmount);
    }
}
