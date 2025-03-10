package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.member.entity.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class TripListDto {
    private Long id;
    private String name;
    private String introduce;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long ownerid;
    private List<TripMemberListRes.MemberResDto> members;
    private Integer totalExpense;
    private List<String> expenseImageUrls;
}
