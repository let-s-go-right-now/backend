package com.lets.go.right.now.domain.trip.repository;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TripRepository extends JpaRepository<Trip, Long> {
    default Trip getTripById(Long tripId) {
        return findById(tripId).orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));
    }
    Optional<Trip> findById(Long tripId);
    @Query("SELECT t FROM Trip t WHERE t.owner = :owner AND t.startDate <= :today AND t.endDate >= :today")
    List<Trip> findOngoingTrips(Member owner, LocalDate today);
}
