package com.lets.go.right.now.domain.trip.repository;

import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {
    default Trip getTripById(Long tripId) {
        return findById(tripId).orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));
    }
    Optional<Trip> findById(Long tripId);
}
