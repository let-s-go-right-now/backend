package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.TripMember;

import java.util.List;
import java.util.stream.Collectors;

public record TripMemberListRes(List<MemberResDto> tripMembers) {

    // 각 멤버의 정보를 담는 DTO
    public record MemberResDto(String name, String profileImageLink) {
        public MemberResDto(Member member) {
            this(member.getName(), member.getProfileImgLink());
        }
    }

    // TripMember 리스트를 MemberResDto 리스트로 변환
    public static TripMemberListRes from(List<TripMember> tripMembers) {
        List<MemberResDto> memberResDtos = tripMembers.stream()
                .map(tripMember -> new MemberResDto(tripMember.getMember()))
                .collect(Collectors.toList());
        return new TripMemberListRes(memberResDtos);
    }
}
