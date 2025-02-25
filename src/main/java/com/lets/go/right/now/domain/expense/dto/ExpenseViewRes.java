package com.lets.go.right.now.domain.expense.dto;

import com.lets.go.right.now.domain.expense.entity.enums.Category;
import java.util.List;

/**
 * 지출 기록 상세 정보 보기
 * 카테고리, 제목, 금액, 상세 내역, 이미지 주소 배열, 결제자, 지출에 포함된 멤버(회원의 프로필 이미지, 이름, 이메일)
 */
public record ExpenseViewRes(
        Category category,
        String name,
        Integer price,
        String details,
        List<String> expenseImages,
        MemberProfileViewRes payer,
        List<MemberProfileViewRes> includedMember
) {
}
