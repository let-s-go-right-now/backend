package com.lets.go.right.now.domain.trip.dto;

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

    public TripListDto(Long id, String name, String introduce, LocalDate startDate, LocalDate endDate, Long ownerid,
                       List<TripMemberListRes.MemberResDto> members, Integer totalExpense, List<String> expenseImageUrls) {
        this.id = id;
        this.name = name;
        this.introduce = introduce;
        this.startDate = startDate;
        this.endDate = endDate;
        this.ownerid = ownerid;
        this.members = members;
        this.totalExpense = totalExpense;
        this.expenseImageUrls = expenseImageUrls;
    }
}
