package com.lets.go.right.now.domain.trip.controller;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.service.MemberService;
import com.lets.go.right.now.domain.trip.dto.TripCreateRequest;
import com.lets.go.right.now.domain.trip.dto.TripDetailResponse;
import com.lets.go.right.now.domain.trip.dto.TripExpenseImageDto;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.service.TripService;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/trip")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final MemberService memberService;

    // 새로운 여행 생성
    @PostMapping
    public ResponseEntity<Trip> createTrip(
            @RequestBody TripCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        String email = customUserDetails.getEmail();
        Member owner = memberService.findByEmail(email);

        LocalDate start = LocalDate.parse(request.getStartDate());
        LocalDate end = LocalDate.parse(request.getEndDate());

        // 여행 생성
        Trip trip = tripService.createTrip(
                request.getName(),
                request.getIntroduce(),
                start,
                end,
                owner
        );
        return ResponseEntity.ok(trip);
    }


}
