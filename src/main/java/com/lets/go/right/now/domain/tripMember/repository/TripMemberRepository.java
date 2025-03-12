package com.lets.go.right.now.domain.tripMember.repository;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.global.enums.Status;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
    List<TripMember> findByTrip(Trip trip);

    default TripMember getByTripAndMember(Trip trip, Member member) {
        return findByTripAndMember(trip, member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_MEMBER_NOT_FOUND));
    }

    Optional<TripMember> findByTripAndMember(Trip trip, Member member);

    List<TripMember> findByMemberId(Long memberId);

    // 목적에 맞는 여행 조회
    // PROGRESS = 정산 진행중 - 진행중인 여행
    // DONE = 정산 완료 - 이전 여행
    @Query("SELECT tm FROM TripMember tm WHERE tm.member = :member and tm.settlementStatus = :status")
    List<TripMember> findMyTripByStatus(@Param("member") Member member, @Param("status") Status status);
}
