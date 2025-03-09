package com.lets.go.right.now.domain.trip.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripCreateRequest {
    private String name;
    private String introduce;
    private String startDate; // 예: "2025-02-16"
    private String endDate;   // 예: "2025-02-18"
}
