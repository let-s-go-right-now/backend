package com.lets.go.right.now.domain.invite.controller;

import com.lets.go.right.now.domain.invite.dto.InviteLinkRes;
import com.lets.go.right.now.domain.invite.service.InviteService;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trip")
public class InviteController {

    private final InviteService inviteService;
    private final MemberRepository memberRepository;

    public InviteController(InviteService inviteService, MemberRepository memberRepository) {
        this.inviteService = inviteService;
        this.memberRepository = memberRepository;
    }

    // 초대 링크 생성
    @PostMapping("/{trip_id}/invite")
    public ResponseEntity<InviteLinkRes> generateInviteLink(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @PathVariable("trip_id") Long tripId) {

        // 1. 요청 하는 회원의 이메일로 Member 조회
        Member loggedInMember = memberRepository.getMemberByEmail(customUserDetails.getEmail());

        // 2. 초대 링크 발급을 요청 하는 회원이 해당 여행의 방장인지 확인
        if (!inviteService.isUserAuthorizedToInvite(loggedInMember, tripId)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 3. 초대 링크 생성
        String inviteLink = inviteService.generateInviteLink(tripId);
        return ResponseEntity.ok(new InviteLinkRes(inviteLink));
    }
}
