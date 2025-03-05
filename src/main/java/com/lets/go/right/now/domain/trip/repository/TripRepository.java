package com.lets.go.right.now.domain.trip.repository;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    default Trip getTripById(Long tripId) {
        return findById(tripId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));
    }

    Optional<Trip> findById(Long tripId);

    // 현재 사용자가 소유한 진행 중인 여행 조회 (startDate <= now <= endDate)
    List<Trip> findByOwnerAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Member owner, LocalDate currentDate1, LocalDate currentDate2
    );

    // 이전(종료)된 여행 조회 : endDate < now
    List<Trip> findByOwnerAndEndDateLessThan(Member owner, LocalDate date);

}

