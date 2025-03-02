package com.lets.go.right.now.domain.expense.dto;

import java.util.List;

/**
 * 여행의 모든 지출 현황 반환
 * 여행 총 지출액, 참여자 수, 각 회원의 총 지출액
 */
public record TravelTotalExpense(
        Integer totalAmount, // 총 지출액의 합
        Integer memberCount, // 여행 참여자 수
        List<MemberTotalExpenseRes> memberTotalExpenses // 각 회원별 지출 총합
) {
    public static TravelTotalExpense of(
            Integer totalAmount, Integer memberCount, List<MemberTotalExpenseRes> memberTotalExpenses) {
        return new TravelTotalExpense(totalAmount, memberCount, memberTotalExpenses);
    }
}
