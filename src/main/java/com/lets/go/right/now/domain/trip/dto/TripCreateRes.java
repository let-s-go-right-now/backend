package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.trip.entity.Trip;

public record TripCreateRes(
        Long tripId
) {
    public static TripCreateRes of(Trip trip) {
        return new TripCreateRes(trip.getId());
    }
}
