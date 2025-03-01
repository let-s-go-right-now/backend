package com.lets.go.right.now.domain.settlement.dto;

import com.lets.go.right.now.domain.expense.dto.MemberProfileViewRes;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.settlement.entity.TravelSettlement;

/**
 * 여행 정산 결과에 대한 정보
 */
public record TravelSettlementResult(
        MemberProfileViewRes sender, // 돈을 보낼 사람
        MemberProfileViewRes receiver, // 돈을 받을 사람
        Integer amount // 보내야 할 돈
) {
    public static TravelSettlementResult of(Member senderEntity, Member receiverEntity, Integer amount) {
        MemberProfileViewRes sender = MemberProfileViewRes.of(senderEntity);
        MemberProfileViewRes receiver = MemberProfileViewRes.of(receiverEntity);
        return new TravelSettlementResult(sender, receiver, amount);
    }
}
