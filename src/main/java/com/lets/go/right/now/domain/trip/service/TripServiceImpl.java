package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.dto.TripDetailResponse;
import com.lets.go.right.now.domain.trip.dto.TripListDto;
import com.lets.go.right.now.domain.trip.dto.TripMemberListRes;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.domain.tripMember.repository.TripMemberRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final MemberRepository memberRepository;

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
        TripMember tripMember = TripMember.toEntity(trip, owner);
        tripMemberRepository.save(tripMember);
        return tripRepository.save(trip);
    }

    // 진행중 여행 목록 조회
    @Transactional(readOnly = true)
    @Override
    public List<TripListDto> getOngoingTrips(Member owner) {
        LocalDate today = LocalDate.now();

        // 리포지토리 메서드를 사용해 진행 중인 여행을 조회
        List<Trip> ongoingTrips = tripRepository.findOngoingTrips(owner, today);

        // DTO로 변환하여 반환
        return ongoingTrips.stream()
                .map(trip -> {
                    // Trip 객체를 직접 TripListDto로 변환
                    TripListDto dto = new TripListDto();
                    dto.setId(trip.getId());
                    dto.setName(trip.getName());
                    dto.setIntroduce(trip.getIntroduce());
                    dto.setStartDate(trip.getStartDate());
                    dto.setEndDate(trip.getEndDate());
                    dto.setOwnerid(trip.getOwner().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 종료된 여행 목록 조회
    @Transactional(readOnly = true)
    @Override
    public List<TripListDto> getEndedTrips(Member owner) {
        LocalDate today = LocalDate.now();

        // 리포지토리 메서드를 사용해 종료된 여행을 조회
        List<Trip> endedTrips = tripRepository.findEndedTrips(owner, today);

        // DTO로 변환하여 반환
        return endedTrips.stream()
                .map(trip -> {
                    TripListDto dto = new TripListDto();
                    dto.setId(trip.getId());
                    dto.setName(trip.getName());
                    dto.setIntroduce(trip.getIntroduce());
                    dto.setStartDate(trip.getStartDate());
                    dto.setEndDate(trip.getEndDate());
                    dto.setOwnerid(trip.getOwner().getId());
                    return dto;
                })
                .collect(Collectors.toList());
    }



    // 특정 여행 상세 조회(이전, 진행중 모두)
    @Transactional(readOnly = true)
    @Override
    public TripDetailResponse getTripDetail(Long tripId, Member member) {
        Trip trip = tripRepository.getTripById(tripId);
        return convertTripToDetailResponse(trip);
    }

    @Override
    public ResponseEntity<?> getTripMembers(String email, Long tripId) {

        // 1. 이메일을 기반으로 회원을 조회
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. 여행(tripId)을 기반으로 해당 여행을 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));

        // 3. 여행에 등록된 멤버인지 확인
        Optional<TripMember> tripMember = tripMemberRepository.findByTripAndMember(trip, member);
        if (tripMember.isEmpty()) {
            // 사용자가 해당 여행에 등록된 멤버가 아닐 경우
            throw new GeneralException(ErrorStatus._TRIP_MEMBER_NOT_FOUND);
        }

        // 4. 해당 여행에 등록된 멤버들을 조회
        List<TripMember> tripMembers = tripMemberRepository.findByTrip(trip);

        // 5. 멤버들을 DTO로 변환
        TripMemberListRes tripMemberResDtoList = TripMemberListRes.from(tripMembers);

        // 6. 조회된 여행 멤버 리스트를 반환
        return ResponseEntity.ok(ApiResponse.onSuccess(tripMemberResDtoList));
    }

    // private 메서드로 중복 코드 제거: Trip -> TripDetailResponse 변환
    private TripDetailResponse convertTripToDetailResponse(Trip trip) {
        TripDetailResponse response = new TripDetailResponse();
        response.setTripId(trip.getId());
        response.setName(trip.getName());
        response.setIntroduce(trip.getIntroduce());
        response.setStartDate(trip.getStartDate().toString());
        response.setEndDate(trip.getEndDate().toString());

        // 여행 방장(owner) 정보 추가
        if (trip.getOwner() != null) {
            response.setOwner(trip.getOwner());
        }

        return response;
    }

}
