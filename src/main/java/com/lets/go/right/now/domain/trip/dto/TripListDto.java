package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class TripListDto {
    private Long id;
    private String name;
    private String introduce;
    private LocalDate startDate;
    private LocalDate endDate;
    private Member owner;
}
