package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.member.entity.Member;

import java.util.List;

public record TripMemberListRes(List<MemberResDto> members) {

    // 각 멤버의 정보를 담는 DTO
    public record MemberResDto(String name, String profileImageLink) {
        public MemberResDto(Member member) {
            this(member.getName(), member.getProfileImgLink());
        }
    }
}
