package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.expense.dto.ExpensePreviewRes;
import com.lets.go.right.now.domain.expense.dto.MemberProfileViewRes;
import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.TripImage;
import com.lets.go.right.now.domain.expense.repository.ExpenseRepository;
import com.lets.go.right.now.domain.expense.repository.TripImageRepository;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.settlement.entity.PersonalSpending;
import com.lets.go.right.now.domain.trip.dto.*;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.trip.enums.SortOption;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.domain.tripMember.repository.TripMemberRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final TripImageRepository tripImageRepository;
    private final MemberRepository memberRepository;
    private final ExpenseRepository expenseRepository;


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

    // 특정 여행 상세 조회 (이전 여행, 진행 중인 여행 모두 해당)
    @Transactional(readOnly = true)
    @Override
    public ResponseEntity<?> getTripDetail(Long tripId) {

        // 여행 정보 조회
        Trip trip = tripRepository.getTripById(tripId);

        // 여행 멤버 조회 및 DTO 변환
        List<TripMember> tripMembers = tripMemberRepository.findByTrip(trip);
        List<TripMemberListRes.MemberResDto> memberDtos = tripMembers.stream()
                .map(tripMember -> new TripMemberListRes.MemberResDto(tripMember.getMember()))
                .collect(Collectors.toList());

        // 여행 총 지출액 계산
        int totalExpense = expenseRepository.findByTrip(trip).stream()  // trip 객체로 Expense 조회
                .mapToInt(Expense::getPrice)  // 각 Expense의 price를 가져옴
                .sum();  // 합산

        // 여행의 모든 지출 정보 가져오기
        List<TripDetailDto.ExpenseResDto> expenseDtos = trip.getExpenses().stream()
                .map(expense -> TripDetailDto.ExpenseResDto.builder()
                        .expenseName(expense.getExpenseName())  // 지출 이름
                        .price(expense.getPrice())              // 지출 금액
                        .expenseDate(expense.getExpenseDate())  // 지출 날짜
                        .category(expense.getCategory().name()) // 지출 카테고리
                        .imageUrls(expense.getTripImages().stream()  // 지출 이미지 URL
                                .map(TripImage::getImageUrl)
                                .collect(Collectors.toList()))
                        .build()
                )
                .collect(Collectors.toList());

        // DTO 반환
        TripDetailDto response = TripDetailDto.builder()
                                    .id(trip.getId())                  // 여행 ID
                                    .name(trip.getName())              // 여행 이름
                                    .introduce(trip.getIntroduce())    // 여행 소개
                                    .startDate(trip.getStartDate())    // 여행 시작 날짜
                                    .endDate(trip.getEndDate())        // 여행 종료 날짜
                                    .ownerId(trip.getOwner().getId())  // 방장 ID
                                    .members(memberDtos)               // 여행 멤버 리스트
                                    .totalExpense(totalExpense)        // 총 지출 금액
                                    .expenses(expenseDtos)             // 지출 내역
                                    .build();

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccess(response));
    }

    @Transactional
    @Override
    public ResponseEntity<?> deleteTripMember(Long tripId, Long targetMemberId, String email) {

        // 1. 해당 여행 정보 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));

        // 2. 해당 여행의 방장 정보 조회
        Member tripOwner = memberRepository.getMemberByEmail(email);

        // 3. 삭제 요청 하는 회원이 방장인지 확인
        if (!trip.getOwner().equals(tripOwner)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN); // 방장 권한인 회원만 멤버 삭제 가능
        }

        // 4. 삭제할 멤버 정보 조회
        Member targetMember = memberRepository.getMemberById(targetMemberId);

        // 5. 해당 여행에 등록된 멤버인지 확인
        TripMember tripMember = tripMemberRepository.findByTripAndMember(trip, targetMember)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_MEMBER_NOT_FOUND));

        // 6. 해당 멤버 삭제
        tripMemberRepository.delete(tripMember);

        return ResponseEntity.ok(ApiResponse.onSuccess("해당 여행의 멤버에서 삭제되었습니다."));
    }

    /**
     * 여행 참여자 조회
     */
    @Override
    public ResponseEntity<?> getTripMembers(Long tripId) {
        // 1. 여행 실존 여부 확인
        Trip trip = tripRepository.getTripById(tripId);
        // 2. 여행 참여자 조회
        List<Member> memberList = trip.getMemberList().stream().map(TripMember::getMember).toList();
        // 3. 반환 DTO 생성 및 반환
        List<MemberProfileViewRes> tripMembers
                = memberList.stream().map(MemberProfileViewRes::of).toList();
        Member owner = trip.getOwner();
        return ResponseEntity.ok(ApiResponse.onSuccess(TripParticipantsRes.of(owner,tripMembers)));
    }

    @Transactional
    public ResponseEntity<?> delegateTripOwner(Long tripId, Long newOwnerId, String email) {

        // 1. 해당 여행 정보 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));

        // 2. 해당 여행의 방장 정보 조회
        Member tripOwner = memberRepository.getMemberByEmail(email);

        // 3. 위임 요청 하는 회원이 방장인지 확인
        if (!trip.getOwner().equals(tripOwner)) {
            throw new GeneralException(ErrorStatus._FORBIDDEN); // 방장 권한인 회원만 위임 가능
        }

        // 4. 새 방장이 될 멤버 조회
        Member newOwner = memberRepository.getMemberById(newOwnerId);

        // 5. 새 방장이 될 멤버가 해당 여행의 멤버인지 확인
        TripMember newOwnerMember = tripMemberRepository.findByTripAndMember(trip, newOwner)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_MEMBER_NOT_FOUND));

        // 6. 방장 권한 위임
        trip.changeOwner(newOwner);
        tripRepository.save(trip);
        tripMemberRepository.save(newOwnerMember);

        // 5. 새 방장 정보를 DTO로 변환
        DelegateOwnerRes.MemberDTO newOwnerDTO = DelegateOwnerRes.MemberDTO.fromMember(newOwner);
        return ResponseEntity.ok(ApiResponse.onSuccess(new DelegateOwnerRes(newOwnerDTO)));

    }

    /**
     * 특정 여행에 대한 지출 목록 조회
     */
    @Override
    public ResponseEntity<?> getTripExpenses(Long tripId, int size, int page, SortOption option) {
        // 1. 여행 존재 여부 조회
        Trip trip = tripRepository.getTripById(tripId);
        // 2. 여행 지출 조회(정렬 기준 적용)
        Pageable pageable = getSortPageable(page, size, option);

        Page<Expense> tripExpenses = expenseRepository.findByTrip(trip, pageable);

        // 3. 반환 DTO 생성 및 반환
        ArrayList<ExpensePreviewRes> resultDtoArray = new ArrayList<>();
        for (Expense expense : tripExpenses.getContent()) {
            resultDtoArray.add(ExpensePreviewRes.of(expense));
        }
        return ResponseEntity.ok(ApiResponse.onSuccess(resultDtoArray));

    }

    // 주어진 옵션에 맞춰 정렬 기준 생성
    private Pageable getSortPageable(int page, int size, SortOption option) {
        if (size < 1) {
            size = 4;
        }
        Sort sort;
        switch (option) {
            case LATEST -> sort = Sort.by(Sort.Direction.DESC, "expenseDate");
            case OLDEST -> sort = Sort.by(Sort.Direction.ASC, "expenseDate");
            case HIGHEST_EXPENSE -> sort = Sort.by(Sort.Direction.DESC, "price");
            case LOWEST_EXPENSE -> sort = Sort.by(Sort.Direction.ASC, "price");
            default -> sort = Sort.by(Sort.Direction.DESC, "expenseDate"); // 기본값: 최신순
        }
        return PageRequest.of(page, size, sort);
    }
}
