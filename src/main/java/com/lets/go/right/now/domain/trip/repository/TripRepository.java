package com.lets.go.right.now.domain.trip.repository;

import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    default Trip getTripById(Long tripId) {
        return findById(tripId).orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));
    }
    Optional<Trip> findById(Long tripId);

    // 정산 상태 PROGRESS 여행 조회
    @Query("SELECT t FROM Trip t JOIN t.travelSettlements ts WHERE ts.settlementStatus = 'PROGRESS' AND t.id = :tripId")
    List<Trip> findOngoingTripsByTripId(@Param("tripId") Long tripId);

    // 정산 상태 DONE 여행 조회
    @Query("SELECT t FROM Trip t JOIN t.travelSettlements ts WHERE ts.settlementStatus = 'DONE' AND t.id = :tripId")
    List<Trip> findEndedTripsByTripId(@Param("tripId") Long tripId);

}
