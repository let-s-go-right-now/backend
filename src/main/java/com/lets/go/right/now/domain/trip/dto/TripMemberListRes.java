package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.member.entity.Member;

import java.util.List;

public record TripMemberListRes(List<MemberResDto> members) {

    // 각 멤버의 정보를 담는 DTO
    public record MemberResDto(Long id, String name, String profileImageLink) {
        public MemberResDto(Member member) {
            this(
                    member.getId(),              // 고유 식별자 추가
                    member.getName(),
                    member.getProfileImgLink()
            );
        }
    }
}
