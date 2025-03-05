package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.trip.dto.TripDetailResponse;
import com.lets.go.right.now.domain.trip.dto.TripExpenseImageDto;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripExpenseImage;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.trip.repository.TripExpenseImageRepository;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.tripMember.dto.TripMemberDto;
import com.lets.go.right.now.domain.tripMember.repository.TripMemberRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripExpenseImageRepository tripExpenseImageRepository;
    private final TripMemberRepository tripMemberRepository;

    @Transactional
    @Override
    public Trip createTrip(String name, String introduce, LocalDate startDate, LocalDate endDate, Member owner) {
        Trip trip = Trip.builder()
                .name(name)
                .introduce(introduce)
                .startDate(startDate)
                .endDate(endDate)
                .owner(owner)
                .build();
        return tripRepository.save(trip);
    }

    @Override
    public Trip getTripById(Long tripId) {
        return tripRepository.getTripById(tripId);
    }

    @Override
    public void deleteTrip(Long tripId) {
        tripRepository.deleteById(tripId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Trip> getOngoingTrips(Member owner) {
        LocalDate now = LocalDate.now();
        return tripRepository.findByOwnerAndStartDateLessThanEqualAndEndDateGreaterThanEqual(owner, now, now);
    }

    @Transactional(readOnly = true)
    @Override
    public TripDetailResponse getTripDetail(Long tripId, Member member) {
        Trip trip = tripRepository.getTripById(tripId);
        return convertTripToDetailResponse(trip);
    }

    // 지출 이미지 조회 로직
    @Transactional(readOnly = true)
    @Override
    public List<TripExpenseImageDto> getTripExpenseImages(Long tripId) {
        List<TripExpenseImage> images = tripExpenseImageRepository.findByTripId(tripId);
        return images.stream()
                .map(img -> {
                    TripExpenseImageDto dto = new TripExpenseImageDto();
                    dto.setImageId(img.getId());
                    dto.setImageUrl(img.getImageUrl());
                    dto.setUploadedAt(img.getUploadedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 이전 여행 목록 조회
    @Transactional(readOnly = true)
    @Override
    public List<Trip> getPastTrips(Member owner) {
        LocalDate today = LocalDate.now();
        return tripRepository.findByOwnerAndEndDateLessThan(owner, today);
    }

    // 이전 여행 상세 조회
    @Transactional(readOnly = true)
    @Override
    public TripDetailResponse getPastTripDetail(Long tripId, Member member) {
        Trip trip = tripRepository.getTripById(tripId);
        LocalDate today = LocalDate.now();
        if (!trip.getEndDate().isBefore(today)) {
            throw new GeneralException(ErrorStatus._TRIP_NOT_FOUND);
        }
        return convertTripToDetailResponse(trip);
    }

    // 이전 여행 멤버 조회
    @Transactional(readOnly = true)
    @Override
    public List<TripMemberDto> getPastTripMembers(Long tripId, Member requester) {
        Trip trip = tripRepository.getTripById(tripId);
        LocalDate today = LocalDate.now();
        if (!trip.getEndDate().isBefore(today)) {
            throw new GeneralException(ErrorStatus._BAD_REQUEST);
        }
        List<TripMember> tripMembers = tripMemberRepository.findByTripId(tripId);
        return tripMembers.stream()
                .map(tm -> {
                    Member m = tm.getMember();
                    TripMemberDto dto = new TripMemberDto();
                    dto.setMemberId(m.getId());
                    dto.setName(m.getName());
                    dto.setEmail(m.getEmail());
                    dto.setSettlementStatus(tm.getSettlementStatus());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // private 메서드로 중복 코드 제거: Trip -> TripDetailResponse 변환
    private TripDetailResponse convertTripToDetailResponse(Trip trip) {
        TripDetailResponse response = new TripDetailResponse();
        response.setTripId(trip.getId());
        response.setName(trip.getName());
        response.setIntroduce(trip.getIntroduce());
        response.setStartDate(trip.getStartDate().toString());
        response.setEndDate(trip.getEndDate().toString());
        return response;
    }
}
