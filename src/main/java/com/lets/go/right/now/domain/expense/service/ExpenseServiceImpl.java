package com.lets.go.right.now.domain.expense.service;

import com.lets.go.right.now.domain.expense.dto.ExpenseCreateReq;
import com.lets.go.right.now.domain.expense.entity.ExcludedMember;
import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.TripImage;
import com.lets.go.right.now.domain.expense.repository.ExcludedMemberRepository;
import com.lets.go.right.now.domain.expense.repository.ExpenseRepository;
import com.lets.go.right.now.domain.expense.repository.TripImageRepository;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.repository.TripMemberRepository;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.global.s3.service.S3Service;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
        // 2. 지출 정보 생성
        Expense expense = ExpenseCreateReq.of(expenseCreateReq);
        expenseRepository.save(expense);
        // 3. 지출 연관 이미지 저장
        for (MultipartFile image : images) {
            String imageUrl = s3Service.uploadFile(image);
            TripImage tripImage = TripImage.toEntity(imageUrl, expense);
            tripImageRepository.save(tripImage);
        }
        // 4. 결제자 설정
        // 4.1. 결제자 존재 여부 파악
        Member payer = memberRepository.getMemberByEmail(expenseCreateReq.payerEmail());
        expense.changePayer(payer);

        // 5. 지출 제외자 탐색
        List<String> excludedMemberEmailList = expenseCreateReq.excludedMember();
        for (String excludedMemberEmail : excludedMemberEmailList) {
            Member member = memberRepository.getMemberByEmail(excludedMemberEmail);
            // 5.1. 지출 제외자 정보 저장
            ExcludedMember excludedMember = ExcludedMember.toEntity(member, expense);
            excludedMemberRepository.save(excludedMember);
        }

        // 6. 정산 결과 데이터 생성 - 어떤 여행에서, 누가 누구에게 돈을 보내야 하는지
        // 6.1. 정산 대상자 탐색
        // - 지출 제외자를 제외한 회원들에게 지출 금액을 나누어 할당
        // - 본인 부담금도 저장 -> 나의 지출 금액으로 확인 가능
//        ArrayList<Member> tripMemberList = tripMemberRepository.findTripMembersByTrip(trip);
//        int participantsCount = tripMemberList.size() - excludedMemberEmailList.size(); // 참여자 수
//        // 정산 금액
//        for (Member participant : tripMemberList) {
//
//        }
        return null;
    }
}
