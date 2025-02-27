package com.lets.go.right.now.domain.settlement.repository;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.settlement.entity.TravelSettlement;
import com.lets.go.right.now.domain.trip.entity.Trip;
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
}
