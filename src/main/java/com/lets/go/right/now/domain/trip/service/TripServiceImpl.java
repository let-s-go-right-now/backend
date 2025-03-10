package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.expense.entity.TripImage;
import com.lets.go.right.now.domain.expense.repository.TripImageRepository;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.settlement.entity.PersonalSpending;
import com.lets.go.right.now.domain.trip.dto.TripDetailResponse;
import com.lets.go.right.now.domain.trip.dto.TripListDto;
import com.lets.go.right.now.domain.trip.dto.TripMemberListRes;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.domain.tripMember.repository.TripMemberRepository;
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
    private final TripMemberRepository tripMemberRepository;
    private final TripImageRepository tripImageRepository;

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

    @Transactional(readOnly = true)
    @Override
    public List<TripListDto> getOngoingTrips(Member owner) {
        LocalDate today = LocalDate.now();

        // 진행 중인 여행 조회
        List<Trip> ongoingTrips = tripRepository.findOngoingTrips(owner, today);

        // DTO 변환 및 여행 멤버, 지출 내역, 지출 이미지 추가
        return ongoingTrips.stream()
                .map(trip -> {
                    TripListDto dto = new TripListDto();
                    dto.setId(trip.getId());
                    dto.setName(trip.getName());
                    dto.setIntroduce(trip.getIntroduce());
                    dto.setStartDate(trip.getStartDate());
                    dto.setEndDate(trip.getEndDate());
                    dto.setOwnerid(trip.getOwner().getId());

                    // 여행 멤버 조회 및 DTO 변환
                    List<TripMember> tripMembers = tripMemberRepository.findByTrip(trip);
                    List<TripMemberListRes.MemberResDto> memberDtos = tripMembers.stream()
                            .map(tripMember -> new TripMemberListRes.MemberResDto(tripMember.getMember()))
                            .collect(Collectors.toList());

                    dto.setMembers(memberDtos);

                    // 여행 총 지출액 계산
                    int totalExpense = trip.getPersonalSpendings().stream()
                            .mapToInt(PersonalSpending::getAmount)
                            .sum();
                    dto.setTotalExpense(totalExpense); // 총 지출액 추가

                    // 여행 지출과 연관된 이미지 조회
                    List<TripImage> tripImages = tripImageRepository.findAllByTrip(trip);
                    List<String> expenseImageUrls = tripImages.stream()
                            .map(TripImage::getImageUrl)
                            .collect(Collectors.toList());

                    dto.setExpenseImageUrls(expenseImageUrls); // 지출 이미지 URL 추가

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

                    // 여행 멤버 조회 및 DTO 변환
                    List<TripMember> tripMembers = tripMemberRepository.findByTrip(trip);
                    List<TripMemberListRes.MemberResDto> memberDtos = tripMembers.stream()
                            .map(tripMember -> new TripMemberListRes.MemberResDto(tripMember.getMember()))
                            .collect(Collectors.toList());

                    dto.setMembers(memberDtos);
                    // 여행 총 지출액 계산
                    int totalExpense = trip.getPersonalSpendings().stream()
                            .mapToInt(PersonalSpending::getAmount)
                            .sum();
                    dto.setTotalExpense(totalExpense); // 총 지출액 추가

                    // 여행 지출과 연관된 이미지 조회
                    List<TripImage> tripImages = tripImageRepository.findAllByTrip(trip);
                    List<String> expenseImageUrls = tripImages.stream()
                            .map(TripImage::getImageUrl)
                            .collect(Collectors.toList());

                    dto.setExpenseImageUrls(expenseImageUrls); // 지출 이미지 URL 추가

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 특정 여행 상세 조회(이전, 진행중 모두)
    @Transactional(readOnly = true)
    @Override
    public TripDetailResponse getTripDetail(Long tripId, Member member) {
        Trip trip = tripRepository.getTripById(tripId);

        // 여행 멤버 조회
        List<TripMember> tripMembers = tripMemberRepository.findByTrip(trip);
        TripMemberListRes tripMemberResDtoList = TripMemberListRes.from(tripMembers);

        return TripDetailResponse.of(trip, tripMemberResDtoList);
    }
}
