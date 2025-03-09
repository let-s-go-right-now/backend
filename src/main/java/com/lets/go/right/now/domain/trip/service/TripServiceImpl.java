package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.dto.TripListDto;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.domain.tripMember.repository.TripMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 변경된 부분

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
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
                    dto.setName(trip.getName());
                    dto.setIntroduce(trip.getIntroduce());
                    dto.setStartDate(trip.getStartDate());
                    dto.setEndDate(trip.getEndDate());
                    dto.setOwner(trip.getOwner());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
