package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.trip.dto.TripDetailResponse;
import com.lets.go.right.now.domain.trip.dto.TripExpenseImageDto;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripImage;
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
