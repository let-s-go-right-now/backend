package com.lets.go.right.now.domain.settlement.service;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.settlement.dto.PaymentCreateReq;
import com.lets.go.right.now.domain.settlement.entity.PersonalSpending;
import com.lets.go.right.now.domain.settlement.repository.PersonalSpendingRepository;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.global.response.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {
    private final PersonalSpendingRepository personalSpendingRepository;
    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;

    /**
     * 여행 금액 정산 하기
     */
    @Override
    public ResponseEntity<?> calculateTravelSettlement(Long tripId) {
        // 개인별 지출을 종합하여, 최종 정산 금액 계산
        // 누가 누구에게 총 얼마를 송금해야 하는지
        return null;
    }

    /**
     * 미리 걷은 돈 정보 기록 - 수정 필요
     */
    @Override
    public ResponseEntity<?> createPrepayment(
            PaymentCreateReq paymentCreateReq, Long tripId, String senderEmail) {
        // 1. 여행 조회
        Trip trip = tripRepository.getTripById(tripId);
        // 2. 송신자 조회
        Member sender = memberRepository.getMemberByEmail(senderEmail);
        // 3. 수신자 조회
        Member receiver = memberRepository.getMemberByEmail(paymentCreateReq.receiverEmail());
        // 4. 정산 금액(음수) 생성
        PersonalSpending personalSpending =
                PersonalSpending.toEntity(
                        trip, null, -paymentCreateReq.amount(), sender, receiver);
        personalSpendingRepository.save(personalSpending);
        return ResponseEntity.ok(ApiResponse.onSuccess("미리 정산한 금액이 저장되었습니다."));
    }

    /**
     * 정산 현황 기록 - 수정 필요
     */
    @Override
    public ResponseEntity<?> sendTravelSettlement(
            PaymentCreateReq paymentCreateReq, Long tripId, String senderEmail) {
        // 1. 여행 조회
        Trip trip = tripRepository.getTripById(tripId);
        // 2. 송신자 조회
        Member sender = memberRepository.getMemberByEmail(senderEmail);
        // 3. 수신자 조회
        Member receiver = memberRepository.getMemberByEmail(paymentCreateReq.receiverEmail());

        return null;
    }
}
