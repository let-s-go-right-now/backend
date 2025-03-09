package com.lets.go.right.now.domain.trip.service;

import com.lets.go.right.now.domain.trip.dto.TripDetailResponse;
import com.lets.go.right.now.domain.trip.dto.TripExpenseImageDto;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.tripMember.dto.TripMemberDto;

import java.time.LocalDate;
import java.util.List;

public interface TripService {
    Trip createTrip(String name, String introduce, LocalDate startDate, LocalDate endDate, Member owner);
}