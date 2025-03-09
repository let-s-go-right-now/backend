package com.lets.go.right.now.domain.trip.repository;

import com.lets.go.right.now.domain.trip.entity.TripExpenseImage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripExpenseImageRepository {
    // tripId를 기준으로 TripExpenseImage를 조회하는 메서드 정의
    List<TripExpenseImage> findByTripId(Long tripId);
}
