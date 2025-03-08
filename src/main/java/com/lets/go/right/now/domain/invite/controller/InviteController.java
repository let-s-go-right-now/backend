package com.lets.go.right.now.domain.invite.controller;

import com.lets.go.right.now.domain.invite.service.InviteService;
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

    public InviteController(InviteService inviteService) {
        this.inviteService = inviteService;
    }

    // 초대 링크 생성
    @PostMapping("/{trip_id}/invite")
    public ResponseEntity<?> generateInviteLink(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @PathVariable("trip_id") Long tripId) {
        return inviteService.generateInviteLink(customUserDetails.getEmail(), tripId);
    }
}
