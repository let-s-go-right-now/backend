package com.lets.go.right.now.domain.settlement.repository;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.settlement.entity.TravelSettlement;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TravelSettlementRepository extends JpaRepository<TravelSettlement,Long> {
    @Query("SELECT ts FROM TravelSettlement ts "
            + "WHERE ts.trip = :trip "
            + "AND ts.sender = :sender "
            + "AND ts.receiver = :receiver")
    Optional<TravelSettlement> findTravelSettlementInfo(
            @Param("trip") Trip trip, @Param("sender") Member sender, @Param("receiver") Member receiver);

    default TravelSettlement getById(Long travelSettlementId) {
        return findById(travelSettlementId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRAVEL_SETTLEMENT_NOT_FOUND));
    }

    /**
     * 해당 회원과 연관된 정산 결과 조회
     */
    @Query("SELECT ts FROM TravelSettlement ts "
            + "WHERE (ts.sender = :member OR ts.receiver = :member) "
            + "AND ts.trip = :trip")
    List<TravelSettlement> findMyTravelSettlement(@Param("member") Member member, @Param("trip") Trip trip);
}
