package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.member.entity.Member;

public record DelegateOwnerRes(
        MemberDTO newOwner  // 새 방장이 될 멤버의 정보
) {

    public record MemberDTO(Long id, String name, String email) {
        public static MemberDTO fromMember(Member member) {
            return new MemberDTO(member.getId(), member.getName(), member.getProfileImgLink());
        }
    }
}
