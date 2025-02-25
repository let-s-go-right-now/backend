package com.lets.go.right.now.domain.expense.dto;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.enums.Category;
import com.lets.go.right.now.domain.member.entity.Member;
import java.util.ArrayList;
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
    public static ExpenseViewRes of(Expense expense, List<String> expenseImages, Member payer, List<Member> participants) {
        MemberProfileViewRes payerProfile = new MemberProfileViewRes(
                payer.getName(), payer.getEmail(),
                payer.getProfileImgLink());

        ArrayList<MemberProfileViewRes> participantsArray = new ArrayList<>();
        for (Member p : participants) {
            participantsArray.add(new MemberProfileViewRes(p.getName(), p.getEmail(), p.getProfileImgLink()));
        }
        List<MemberProfileViewRes> participantsProfiles = participantsArray.stream().toList();

        return new ExpenseViewRes(
                expense.getCategory(),
                expense.getExpenseName(),
                expense.getPrice(),
                expense.getDetails(),
                expenseImages,
                payerProfile,
                participantsProfiles);
    }
}
