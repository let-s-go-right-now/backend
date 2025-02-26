package com.lets.go.right.now.domain.settlement.service;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.settlement.dto.PrepaymentCreateReq;
import com.lets.go.right.now.domain.settlement.entity.SettlementResult;
import com.lets.go.right.now.domain.settlement.repository.SettlementResultRepository;
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
    private final SettlementResultRepository settlementResultRepository;
    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;

    @Override
    public ResponseEntity<?> createPrepayment(
            PrepaymentCreateReq prepaymentCreateReq, Long tripId, String senderEmail) {
        // 1. 여행 조회
        Trip trip = tripRepository.getTripById(tripId);
        // 2. 송신자 조회
        Member sender = memberRepository.getMemberByEmail(senderEmail);
        // 3. 수신자 조회
        Member receiver = memberRepository.getMemberByEmail(prepaymentCreateReq.receiverEmail());
        // 4. 정산 금액(음수) 생성
        SettlementResult settlementResult =
                SettlementResult.toEntity(
                        trip, null, -prepaymentCreateReq.amount(), sender, receiver);
        settlementResultRepository.save(settlementResult);
        return ResponseEntity.ok(ApiResponse.onSuccess("미리 정산한 금액이 저장되었습니다."));
    }
}
