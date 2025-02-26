package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService{
    private final TripRepository tripRepository;

    @Override
    public ResponseEntity<?> finishSettlement(String email, Long tripId) {
        return null;
    }
}
