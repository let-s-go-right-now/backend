package com.lets.go.right.now.domain.trip.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class TripDetailDto {
    private Long id;
    private String name;
    private String introduce;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long ownerId;
    private List<TripMemberListRes.MemberResDto> members;
    private Integer totalExpense;
    private List<String> expenseImageUrls;
}
