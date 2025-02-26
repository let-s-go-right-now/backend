package com.lets.go.right.now.domain.trip.service;

import org.springframework.http.ResponseEntity;

public interface TripService {
    ResponseEntity<?> finishSettlement(String email, Long tripId);
}
