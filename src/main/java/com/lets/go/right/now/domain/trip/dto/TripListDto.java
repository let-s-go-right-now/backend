package com.lets.go.right.now.domain.trip.dto;

import com.lets.go.right.now.domain.expense.dto.MemberProfileViewRes;
import com.lets.go.right.now.domain.trip.entity.Trip;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public record TripListDto (
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    String ownerEmail,
    List<MemberProfileViewRes> members,
    Integer totalExpense,
    List<String> expenseImageUrls // 지출 관련 이미지
) {
    public static TripListDto of(Trip trip, List<MemberProfileViewRes> members, Integer totalExpense, List<String> expenseImageUrls) {
        return new TripListDto(
                trip.getId(),
                trip.getName(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getOwner().getEmail(),
                members,
                totalExpense,
                expenseImageUrls);
    }

}
