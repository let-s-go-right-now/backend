package com.lets.go.right.now.domain.trip.controller;

import com.lets.go.right.now.domain.trip.service.TripService;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/trip")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PutMapping("{trip_id}/settlement-finish")
    public ResponseEntity<?> finishSettlement(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("trip_id") Long tripId) {
        return tripService.finishSettlement(customUserDetails.getEmail(), tripId);
    }
}
