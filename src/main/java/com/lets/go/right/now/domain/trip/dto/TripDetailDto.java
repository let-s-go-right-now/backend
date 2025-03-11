package com.lets.go.right.now.domain.trip.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TripDetailDto {
    private Long id;
    private String name;
    private String introduce;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long ownerId;
    private List<TripMemberListRes.MemberResDto> members;
    private int totalExpense;
    private List<ExpenseResDto> expenses;

    @Data
    @Builder
    public static class ExpenseResDto {
        private String expenseName;
        private Integer price;
        private LocalDateTime expenseDate;
        private String category;
        private List<String> imageUrls;
    }
}


