package com.lets.go.right.now.domain.invite.controller;

import com.lets.go.right.now.domain.invite.dto.InviteLinkRes;
import com.lets.go.right.now.domain.invite.dto.InviteRes;
import com.lets.go.right.now.domain.invite.service.InviteService;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.enums.statuscode.SuccessStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    // 초대 링크를 통한 여행 멤버 등록
    @PostMapping("/join")
    public ResponseEntity<InviteRes> joinTrip(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam String token) {

        // 로그인된 회원의 이메일로 Member 조회
        Member loggedInMember = memberRepository.getMemberByEmail(customUserDetails.getEmail());
        if (loggedInMember == null) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }

        // 초대 토큰을 이용해 해당 여행 멤버로 등록
        inviteService.joinTripWithInvite(token, loggedInMember.getId());

        // 여행 등록 성공 처리
        return ResponseEntity.ok().body(
                InviteRes.success(SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), "해당 여행의 멤버로 등록되었습니다.")
        );

    }
}
