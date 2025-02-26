package com.lets.go.right.now.domain.tripMember.service;

import org.springframework.http.ResponseEntity;

public interface TripMemberService {
    ResponseEntity<?> finishSettlement(String email, Long tripId);
}
