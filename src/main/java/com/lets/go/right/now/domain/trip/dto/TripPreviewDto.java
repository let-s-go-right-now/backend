package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.trip.entity.Trip;

/**
 * 진행중인 여행 목록 조회시
 */
public record TripPreviewDto(
        Long tripId,
        String name
) {
    public static TripPreviewDto of(Trip trip) {
        return new TripPreviewDto(trip.getId(), trip.getName());
    }
}
