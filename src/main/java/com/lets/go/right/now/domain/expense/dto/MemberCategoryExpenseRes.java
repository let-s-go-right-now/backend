package com.lets.go.right.now.domain.expense.dto;

import com.lets.go.right.now.domain.member.entity.Member;

/**
 * 회원별 특정 카테고리 지출 리포트 DTO
 */
public record MemberCategoryExpenseRes(
        String memberName,
        Integer amount // 지출 금액
) {
    public static MemberCategoryExpenseRes of(Member member, Integer amount) {
        return new MemberCategoryExpenseRes(member.getName(), amount);
    }
}
