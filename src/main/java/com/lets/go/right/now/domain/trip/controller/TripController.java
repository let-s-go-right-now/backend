package com.lets.go.right.now.domain.trip.controller;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.dto.DelegateOwnerReq;
import com.lets.go.right.now.domain.trip.dto.TripCreateRequest;
import com.lets.go.right.now.domain.trip.dto.TripCreateRes;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.enums.SortOption;
import com.lets.go.right.now.domain.trip.service.TripService;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import com.lets.go.right.now.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    // 진행 중인 여행 조회
    @GetMapping("/ongoing")
    public ResponseEntity<?> getOngoingTrips(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return tripService.getOngoingTrips(customUserDetails.getEmail());
    }

    // 이전 여행 목록 조회
    @GetMapping("/ended")
    public ResponseEntity<?> getEndedTrips(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return tripService.getEndedTrips(customUserDetails.getEmail());
    }

    // 특정 여행 상세 조회 (이전 여행, 진행 중인 여행 모두 해당)
    @GetMapping("/{trip_id}")
    public ResponseEntity<?> getTripDetail(
            @PathVariable("trip_id") Long tripId) {
        return tripService.getTripDetail(tripId);
    }

    // 멤버 내보내기 (방장만 가능)
    @DeleteMapping("/{trip_id}/members/{member_id}")
    public ResponseEntity<?> deleteTripMember(@PathVariable("trip_id") Long tripId,
                                              @PathVariable("member_id") Long targetMemberId, // 삭제 대상 멤버
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return tripService.deleteTripMember(tripId, targetMemberId, customUserDetails.getEmail());
    }

    /**
     * 여행 참여자 정보 얻어오기
     */
    @GetMapping("{trip_id}/trip-member")
    public ResponseEntity<?> getTripMembers(
            @PathVariable("trip_id") Long tripId
    ) {
        return tripService.getTripMembers(tripId);
    }

    // 방장 권한 위임 (방장만 가능)
    @PutMapping("/{trip_id}/delegate")
    public ResponseEntity<?> delegateTripOwner(
            @PathVariable("trip_id") Long tripId,
            @RequestBody DelegateOwnerReq req,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return tripService.delegateTripOwner(tripId, req.newOwnerId(), customUserDetails.getEmail());
    }


    /**
     * 특정 여행에 대한 모든 지출 조회
     */
    @GetMapping("{trip_id}/expense")
    public ResponseEntity<?> getTripExpenses(
            @PathVariable("trip_id") Long tripId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "option", defaultValue = "LATEST") String optionStr) {
        // SortOption 변환 및 예외 처리
        SortOption option;
        try {
            option = SortOption.valueOf(optionStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus._INVALID_SORT_OPTION);
        }

        return tripService.getTripExpenses(tripId, page, size, option);
    }

    // 특정 여행 관리 페이지 조회
    @GetMapping("/{trip_id}/info")
    public ResponseEntity<?> getTripInfo(@PathVariable("trip_id") Long tripId) {
        return tripService.getTripInfo(tripId);
    }
}
