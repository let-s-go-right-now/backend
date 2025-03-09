package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.member.entity.Member;
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
    TripDetailResponse getTripDetail(Long tripId, Member member);
    // 특정 여행에 등록된 멤버들 조회
    ResponseEntity<?> getTripMembers(String email, Long tripId);
}
