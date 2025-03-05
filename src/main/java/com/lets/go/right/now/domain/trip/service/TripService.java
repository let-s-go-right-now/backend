package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.trip.dto.TripDetailResponse;
import com.lets.go.right.now.domain.trip.dto.TripExpenseImageDto;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.tripMember.dto.TripMemberDto;

import java.time.LocalDate;
import java.util.List;

public interface TripService {
    Trip createTrip(String name, String introduce, LocalDate startDate, LocalDate endDate, Member owner);
    Trip getTripById(Long tripId);
    void deleteTrip(Long tripId);

    // 진행 중인 여행 목록
    List<Trip> getOngoingTrips(Member owner);

    // 이전(종료)된 여행 목록
    List<Trip> getPastTrips(Member owner);

    // 지출 기록 이미지 조회
    List<TripExpenseImageDto> getTripExpenseImages(Long tripId);

    // 특정 여행 상세
    TripDetailResponse getTripDetail(Long tripId, Member member);

    // 이전 여행 상세 조회
    TripDetailResponse getPastTripDetail(Long tripId, Member member);

    // 이전 여행의 멤버 조회
    List<TripMemberDto> getPastTripMembers(Long tripId, Member requester);
}