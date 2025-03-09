package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.expense.dto.MemberProfileViewRes;
import com.lets.go.right.now.domain.trip.entity.Trip;

public record TripDetailResponse(
        Long tripId,
        String name,
        String introduce,
        String startDate,
        String endDate,
        MemberProfileViewRes owner, // 🔹 Owner 정보를 DTO로 변환
        TripMemberListRes tripMembers // 🔹 여행 멤버 리스트 추가
) {
    public static TripDetailResponse of(Trip trip, TripMemberListRes members) {
        return new TripDetailResponse(
                trip.getId(),
                trip.getName(),
                trip.getIntroduce(),
                trip.getStartDate().toString(),
                trip.getEndDate().toString(),
                MemberProfileViewRes.of(trip.getOwner()), // 🔹 Owner 정보 변환
                members // 🔹 여행 멤버 리스트 포함
        );
    }
}
