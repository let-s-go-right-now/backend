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

    // 진행중인 여행조회
    @GetMapping
    public ResponseEntity<List<Trip>> getOngoingTrips(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        String email = customUserDetails.getEmail();
        Member owner = memberService.findByEmail(email);

        List<Trip> ongoingTrips = tripService.getOngoingTrips(owner);
        return ResponseEntity.ok(ongoingTrips);
    }

    // 특정여행 상세조회
    @GetMapping("/{trip_id}")
    public ResponseEntity<TripDetailResponse> getTripDetail(
            @PathVariable("trip_id") Long tripId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        String email = customUserDetails.getEmail();
        Member member = memberService.findByEmail(email);

        TripDetailResponse response = tripService.getTripDetail(tripId, member);
        return ResponseEntity.ok(response);
    }

    // 지출 기록 이미지 조회
    @GetMapping("/{trip_id}/expense/image")
    public ResponseEntity<List<TripExpenseImageDto>> getTripExpenseImages(
            @PathVariable("trip_id") Long tripId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<TripExpenseImageDto> images = tripService.getTripExpenseImages(tripId);
        return ResponseEntity.ok(images);
    }

    // 이전 여행 목록 조회
    @GetMapping("/past")
    public ResponseEntity<List<Trip>> getPastTrips(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        String email = customUserDetails.getEmail();
        Member owner = memberService.findByEmail(email);

        List<Trip> pastTrips = tripService.getPastTrips(owner);
        return ResponseEntity.ok(pastTrips);
    }

    // 이전 여행 상세 조회
    @GetMapping("/past/{tripId}")
    public ResponseEntity<TripDetailResponse> getPastTripDetail(
            @PathVariable Long tripId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        String email = customUserDetails.getEmail();
        Member member = memberService.findByEmail(email);

        TripDetailResponse response = tripService.getPastTripDetail(tripId, member);
        return ResponseEntity.ok(response);
    }

}
