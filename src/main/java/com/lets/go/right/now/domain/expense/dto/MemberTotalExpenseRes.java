package com.lets.go.right.now.domain.expense.dto;

import com.lets.go.right.now.domain.member.entity.Member;

/**
 * 회원 별 총 지출액 반환 DTO
 */
public record MemberTotalExpenseRes(
        MemberProfileViewRes memberProfile,
        Integer amount
) {
    public static MemberTotalExpenseRes of(Member member, Integer amount) {
        return new MemberTotalExpenseRes(MemberProfileViewRes.of(member), amount);
    }
}
