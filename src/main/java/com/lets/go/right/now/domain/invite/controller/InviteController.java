package com.lets.go.right.now.domain.invite.controller;

import com.lets.go.right.now.domain.invite.service.InviteService;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trip")
public class InviteController {

    private final InviteService inviteService;

    public InviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    // 초대 링크 생성
    @PostMapping("/{trip_id}/invite")
    public ResponseEntity<?> createInviteLink(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                          @PathVariable("trip_id") Long tripId) {
        return inviteService.createInviteLink(customUserDetails.getEmail(), tripId);
    }

    // 초대 링크를 통한 여행 멤버 등록
    @PostMapping("/join")
    public ResponseEntity<?> joinWithInviteLink(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam String token) {
        return inviteService.joinWithInviteLink(customUserDetails.getEmail(), token);
    }
}