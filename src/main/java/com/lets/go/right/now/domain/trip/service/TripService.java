package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.dto.TripDetailDto;
import com.lets.go.right.now.domain.trip.dto.TripDetailResponse;
import com.lets.go.right.now.domain.trip.dto.TripListDto;
import com.lets.go.right.now.domain.trip.entity.Trip;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface TripService {
    Trip createTrip(String name, String introduce, LocalDate startDate, LocalDate endDate, Member owner);

    // 진행중인 여행 목록 조회
    List<TripListDto> getOngoingTrips(Member owner);
    //완료된 여행 목록 조회
    List<TripListDto> getEndedTrips(Member owner);
    //특정 여행 목록 조회
    TripDetailDto getTripDetail(Long tripId, Member member);
    // 멤버 내보내기 (방장만 가능)
    ResponseEntity<?> deleteTripMember(Long tripId, Long targetMemberId, String email);
    // 여행 참여자 정보 조회
    ResponseEntity<?> getTripMembers(Long tripId);
    // 방장 권한 위임하기
    ResponseEntity<?> delegateTripOwner(Long tripId, Long newOwnerId, String email);

    ResponseEntity<?> getTripExpenses(Long tripId, int page, int size);
}
