package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.expense.dto.MemberProfileViewRes;
import com.lets.go.right.now.domain.member.entity.Member;
import java.util.List;

public record TripParticipantsRes(
        MemberProfileViewRes owner, // 여행 방장
        List<MemberProfileViewRes> tripMembers
) {
    public static TripParticipantsRes of(Member owner, List<MemberProfileViewRes> tripMembers) {
        return new TripParticipantsRes(MemberProfileViewRes.of(owner), tripMembers);
    }
}
