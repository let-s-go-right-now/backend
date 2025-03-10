package com.lets.go.right.now.domain.trip.controller;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.dto.*;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.service.TripService;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import com.lets.go.right.now.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/trip")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;
    private final MemberRepository memberRepository;

    // 새로운 여행 생성
    @PostMapping
    public ResponseEntity<?> createTrip(
            @RequestBody TripCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member owner = memberRepository.findMemberByEmail(customUserDetails.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

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
        return ResponseEntity.ok(ApiResponse.onSuccess(TripCreateRes.of(trip)));
    }
    // 진행중인 여행조회
    @GetMapping("/ongoing")
    public ResponseEntity<ApiResponse<List<TripListDto>>> getOngoingTrips(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member owner = memberRepository.findMemberByEmail(customUserDetails.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        List<TripListDto> ongoingTrips = tripService.getOngoingTrips(owner);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.onSuccess(ongoingTrips));
    }
    // 완료된 여행조회
    @GetMapping("/ended")
    public ResponseEntity<ApiResponse<List<TripListDto>>> getEndedTrips(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member owner = memberRepository.findMemberByEmail(customUserDetails.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        List<TripListDto> ongoingTrips = tripService.getEndedTrips(owner);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.onSuccess(ongoingTrips));
    }

    // 특정여행 상세조회
    @GetMapping("/{tripId}")
    public ResponseEntity<TripDetailResponse> getTripDetail(
            @PathVariable("tripId") Long tripId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Member member = memberRepository.findMemberByEmail(customUserDetails.getEmail())
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        TripDetailResponse response = tripService.getTripDetail(tripId, member);
        return ResponseEntity.ok(response);
    }

    // 특정 여행 멤버 내보내기 (방장만 가능)
    @DeleteMapping("/{trip_id}/members/{member_id}")
    public ResponseEntity<?> deleteTripMember(@PathVariable("trip_id") Long tripId,
                                              @PathVariable("member_id") Long targetMemberId, // 삭제 대상 멤버
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return tripService.deleteTripMember(tripId, targetMemberId, customUserDetails.getEmail());
    }

}
