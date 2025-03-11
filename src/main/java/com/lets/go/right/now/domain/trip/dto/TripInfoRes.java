package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public record TripInfoRes(
        Long tripId,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String description,
        List<MemberResDto> tripMembers
) {

    public record MemberResDto(Long id, String name, String profileImageLink) {
        public MemberResDto(Member member) {
            this(member.getId(), member.getName(), member.getProfileImgLink());
        }
    }

    // Trip 객체를 받아 TripInfoRes 객체로 변환
    public static TripInfoRes from(Trip trip) {
        List<MemberResDto> memberResDtos = trip.getMemberList().stream()
                .map(tripMember -> new MemberResDto(tripMember.getMember()))
                .collect(Collectors.toList());

        return new TripInfoRes(
                trip.getId(),
                trip.getName(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getIntroduce(),
                memberResDtos // 변환된 리스트 반환
        );
    }
}

