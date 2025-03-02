package com.lets.go.right.now.domain.settlement.service;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.settlement.dto.PaymentCreateReq;
import com.lets.go.right.now.domain.settlement.dto.TravelSettlementResult;
import com.lets.go.right.now.domain.settlement.dto.TravelSettlementResultReq;
import com.lets.go.right.now.domain.settlement.entity.PersonalSpending;
import com.lets.go.right.now.domain.settlement.entity.TravelSettlement;
import com.lets.go.right.now.domain.settlement.repository.PersonalSpendingRepository;
import com.lets.go.right.now.domain.settlement.repository.TravelSettlementRepository;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.domain.tripMember.repository.TripMemberRepository;
import com.lets.go.right.now.global.enums.Status;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.response.ApiResponse;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
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
    private final TripMemberRepository tripMemberRepository;
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
     * 미리 걷은 돈 반영
     */
    @Override
    public ResponseEntity<?> createPrepayment(
            PaymentCreateReq paymentCreateReq, Long tripId, String senderEmail) {
        // 1. 여행 조회
        Trip trip = tripRepository.getTripById(tripId);
        // 2. 송신자 조회(본인)
        Member sender = memberRepository.getMemberByEmail(senderEmail);
        // 3. 수신자 조회(미리 돈을 받은 자)
        Member receiver = memberRepository.getMemberByEmail(paymentCreateReq.receiverEmail());

        // 미리 걷은 돈을 기록하는 시점은 여행 지출 정산이 완료 된 이후
        // -> 해당하는 여행 지출 정산 엔티티 조회 후 미리 걷은 돈 반영
        TravelSettlement travelSettlement = travelSettlementRepository.findTravelSettlementInfo(trip, sender, receiver)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRAVEL_SETTLEMENT_NOT_FOUND));

        // 미리 걷은 금액(음수)만큼 차감
        travelSettlement.addAmount(-paymentCreateReq.amount());

        return ResponseEntity.ok(ApiResponse.onSuccess("미리 정산한 금액이 반영 되었습니다."));
    }

    /**
     * 정산 현황 기록 - 수정 필요
     */
    @Override
    public ResponseEntity<?> sendTravelSettlement(Long travelSettlementId) {
        TravelSettlement travelSettlement = travelSettlementRepository.getById(travelSettlementId);

        travelSettlement.changeStatus(Status.DONE);

        return ResponseEntity.ok(ApiResponse.onSuccess("정산 완료 처리 되었습니다."));
    }

    /**
     * 여행 지출 결과 보기
     */
    @Override
    public ResponseEntity<?> getTravelSettlementResults(String email, Long travelId) {
        // 회원 조회
        Member member = memberRepository.getMemberByEmail(email);
        // 여행 조회
        Trip trip = tripRepository.getTripById(travelId);
        // 해당 회원이 여행의 일원인지 확인
        TripMember tripMember = tripMemberRepository.getByTripAndMember(trip, member);

        // 본인 부담금도 포함하여 조회
        List<TravelSettlement> myTravelSettlement =
                travelSettlementRepository.findMyTravelSettlement(member,trip);

        Integer totalAmount = 0;
        ArrayList<TravelSettlementResult> resultDtoList = new ArrayList<>();

        for (TravelSettlement travelSettlement : myTravelSettlement) {
            Member sender = travelSettlement.getSender();
            Member receiver = travelSettlement.getReceiver();
            Integer settlementAmount = travelSettlement.getAmount();

            // 본인 부담금(손실) 계산
            if (sender.equals(receiver)) {
                totalAmount += settlementAmount;
                continue;
            }

            // 총 지출 금액 계산
            if (receiver.equals(member)) { // 받을 돈
                totalAmount -= settlementAmount;
            } else { // 보낼 돈
                totalAmount += settlementAmount;
            }
            // 정산 내역 추가
            resultDtoList.add(TravelSettlementResult.of(travelSettlement));
        }

        TravelSettlementResultReq resultDto = TravelSettlementResultReq.of(totalAmount, resultDtoList);
        return ResponseEntity.ok(ApiResponse.onSuccess(resultDto));
    }

    /**
     * 정산 현황 확인하기
     */
    @Override
    public ResponseEntity<?> getTravelSettlementStatus(String email, Long travelId) {
        // 1. 회원 존재 여부 확인

        // 2. 여행 존재 여부 확인

        // 3. 여행 회원 여부 확인

        // 4. 해당 회원의 정산 완료 상태 확인

        // 5. 해당 여행의 모든 정산 현황 조회 및 데이터 가공
        return null;
    }
}
