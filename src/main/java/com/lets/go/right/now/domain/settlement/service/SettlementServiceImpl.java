package com.lets.go.right.now.domain.settlement.service;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.settlement.dto.PaymentCreateReq;
import com.lets.go.right.now.domain.settlement.entity.PersonalSpending;
import com.lets.go.right.now.domain.settlement.entity.TravelSettlement;
import com.lets.go.right.now.domain.settlement.repository.PersonalSpendingRepository;
import com.lets.go.right.now.domain.settlement.repository.TravelSettlementRepository;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.global.response.ApiResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {
    private final PersonalSpendingRepository personalSpendingRepository;
    private final TravelSettlementRepository travelSettlementRepository;
    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;

    /**
     * 여행 금액 정산 하기
     */
    @Override
    public ResponseEntity<?> calculateTravelSettlement(Long tripId) {
        // 1. 여행 존재 여부 확인
        Trip trip = tripRepository.getTripById(tripId);

        // 2. 해당 여행의 개인별 지출 정보 조회
        List<PersonalSpending> spendingList = personalSpendingRepository.findByTrip(trip);

        // 3. (sender_id, receiver_id) 기준으로 그룹화하여 총 송금액 계산
        Map<List<Long>, Integer> groupedSettlement = spendingList.stream()
                .collect(Collectors.groupingBy(
                        spending -> List.of(spending.getSender().getId(), spending.getReceiver().getId()),
                        Collectors.summingInt(PersonalSpending::getAmount)
                ));

        // 4. 계산된 정산 정보 travel_settlement 테이블에 저장
        List<TravelSettlement> settlements = groupedSettlement.entrySet().stream()
                .map(entry -> {
                    Member sender = memberRepository.getMemberById(entry.getKey().get(0));
                    Member receiver = memberRepository.getMemberById(entry.getKey().get(1));
                    return TravelSettlement.toEntity(trip, entry.getValue(), sender, receiver);
                })
                .collect(Collectors.toList());

        travelSettlementRepository.saveAll(settlements);

        return ResponseEntity.ok(ApiResponse.onSuccess("정산이 완료되었습니다."));
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
