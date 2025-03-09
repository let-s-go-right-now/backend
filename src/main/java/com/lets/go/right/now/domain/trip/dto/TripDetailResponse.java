package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TripDetailResponse {
    private Long tripId;
    private String name;
    private String introduce;
    private String startDate;
    private String endDate;
    private Member owner;
}
