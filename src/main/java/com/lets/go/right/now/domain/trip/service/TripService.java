package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.enums.SortOption;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

public interface TripService {
    Trip createTrip(String name, String introduce, LocalDate startDate, LocalDate endDate, Member owner);

    // 진행 중인 여행 목록 조회
    ResponseEntity<?> getOngoingTrips(String email);
    // 이전 여행 목록 조회
    ResponseEntity<?> getEndedTrips(String email);
    // 특정 여행 목록 조회
    ResponseEntity<?> getTripDetail(Long tripId, Long userId);
    // 멤버 내보내기 (방장만 가능)
    ResponseEntity<?> deleteTripMember(Long tripId, Long targetMemberId, String email);
    // 여행 참여자 정보 조회
    ResponseEntity<?> getTripMembers(Long tripId);
    // 방장 권한 위임하기
    ResponseEntity<?> delegateTripOwner(Long tripId, Long newOwnerId, String email);

    ResponseEntity<?> getTripExpenses(Long tripId, int page, int size, SortOption option);
    // 특정 여행 관리 페이지 조회
    ResponseEntity<?> getTripInfo(Long tripId);

    // 여행 종료하기
    ResponseEntity<?> endTrip(String email, Long tripId);
}
