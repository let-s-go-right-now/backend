package com.lets.go.right.now.domain.tripMember.controller;

import com.lets.go.right.now.domain.tripMember.service.TripMemberService;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/trip-member")
@RequiredArgsConstructor
public class TripMemberController {
    private final TripMemberService tripMemberService;

    @PutMapping("{trip_id}/settlement-finish")
    public ResponseEntity<?> finishSettlement(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("trip_id") Long tripId) {
        return tripMemberService.finishSettlement(customUserDetails.getEmail(), tripId);
    }


}
