package com.lets.go.right.now.domain.expense.service;

import com.lets.go.right.now.domain.expense.dto.ExpenseCreateReq;
import com.lets.go.right.now.domain.expense.entity.ExcludedMember;
import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.SettlementResult;
import com.lets.go.right.now.domain.expense.entity.TripImage;
import com.lets.go.right.now.domain.expense.repository.ExcludedMemberRepository;
import com.lets.go.right.now.domain.expense.repository.ExpenseRepository;
import com.lets.go.right.now.domain.expense.repository.SettlementResultRepository;
import com.lets.go.right.now.domain.expense.repository.TripImageRepository;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.trip.repository.TripMemberRepository;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.response.ApiResponse;
import com.lets.go.right.now.global.s3.service.S3Service;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService{
    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final MemberRepository memberRepository;
    private final TripImageRepository tripImageRepository;
    private final SettlementResultRepository settlementResultRepository;
    private final ExcludedMemberRepository excludedMemberRepository;
    private final S3Service s3Service;

    /**
     * 여행 지출 기록 생성
     */
    @Override
    @Transactional
    public ResponseEntity<?> createExpense(
            Long tripId, ExpenseCreateReq expenseCreateReq, List<MultipartFile> images) throws IOException {
        // 1. 여행 존재 여부 확인
        Trip trip = tripRepository.getTripById(tripId);
        // 2. 결제자 정보 확인
        Member payer = memberRepository.getMemberByEmail(expenseCreateReq.payerEmail());

        // 3. 이미지 업로드
        List<TripImage> tripImages = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                String imageUrl = s3Service.uploadFile(image);
                tripImages.add(TripImage.toEntity(imageUrl, null)); // Expense는 나중에 설정
            }
        }
        saveExpenseWithTransaction(trip, expenseCreateReq, payer, tripImages);

        return ResponseEntity.ok(ApiResponse.onSuccess("지출 기록이 생성 되었습니다."));
    }

    // 지출 기록 저장
    @Transactional
    public void saveExpenseWithTransaction(Trip trip, ExpenseCreateReq expenseCreateReq,
                                           Member payer, List<TripImage> tripImages) {
        // 1. Expense 객체 생성 및 저장
        Expense expense = ExpenseCreateReq.of(expenseCreateReq, trip, payer);
        expenseRepository.save(expense);

        // 2. TripImage 객체 저장
        for (TripImage tripImage : tripImages) {
            tripImage.changeExpense(expense);
            tripImageRepository.save(tripImage);
        }

        // 3. 지출 제외 멤버 저장
        List<Member> excludedMembers = new ArrayList<>();
        if (expenseCreateReq.excludedMember() != null) {
            for (String excludedEmail : expenseCreateReq.excludedMember()) {
                // 3.1. 회원 존재 여부 확인
                Member excludedMember = memberRepository.getMemberByEmail(excludedEmail);
                excludedMembers.add(excludedMember);
            }
            excludedMemberRepository.saveAll(excludedMembers.stream()
                    .map(member -> ExcludedMember.toEntity(member, expense))
                    .collect(Collectors.toList()));
        }

        // 4. 정산 결과에 반영
        saveSettlement(trip, expense, payer, excludedMembers);
    }

    @Transactional
    public void saveSettlement(Trip trip, Expense expense, Member payer, List<Member> excludedMembers) {
        // 1. 여행 참여 멤버 조회
        List<TripMember> tripMembers = tripMemberRepository.findByTrip(trip);
        List<Member> participants = tripMembers.stream()
                .map(TripMember::getMember)
                .collect(Collectors.toList());

        // 2. 정산 대상 필터링 (제외 멤버 제거, 결제자는 별도 처리)
        List<Member> actualParticipants = participants.stream()
                .filter(member -> !excludedMembers.contains(member) && !member.equals(payer)) // 결제자 제외
                .collect(Collectors.toList());

        // 정산할 회원이 없는 경우 예외 처리
        if (actualParticipants.isEmpty()) {
            throw new GeneralException(ErrorStatus._SETTLEMENT_MEMBER_NOT_FOUND);
        }

        // 3. 1인당 정산 금액 계산
        int totalAmount = expense.getPrice();
        int settlementAmount = totalAmount / (actualParticipants.size() + 1); // 결제자 포함 인원(+1)
        int remainingAmount = totalAmount % (actualParticipants.size() + 1); // 남은 금액 계산 -

        // 4. 참여자들에게 정산 금액 저장
        for (Member participant : actualParticipants) {
            settlementResultRepository.save(SettlementResult.toEntity(trip, settlementAmount, participant, payer));
        }

        // 5. 결제자에게 남은 금액 포함하여 저장
        settlementResultRepository.save(SettlementResult.toEntity(trip, settlementAmount + remainingAmount, payer, payer));
    }


}
