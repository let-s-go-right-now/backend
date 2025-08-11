package com.lets.go.right.now.domain.tripMember.repository;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.global.enums.Status;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
    @Query("SELECT tm FROM TripMember tm WHERE tm.member = :member AND tm.trip.tripStatus = :status")
    List<TripMember> findMyTripByStatus(Member member, Status status);

    //완료된 여행
    @Query("""
    SELECT tm FROM TripMember tm 
    WHERE tm.member = :member 
      AND (tm.trip.tripStatus = :doneStatus OR tm.trip.endDate < CURRENT_DATE)
""")
    List<TripMember> findMyTripEndedOrPastTrips(@Param("member") Member member, @Param("doneStatus") Status doneStatus);

    // 해당 여행에 대한 특정 멤버의 정산 상태 확인
    @Query("SELECT tm.settlementStatus FROM TripMember tm WHERE tm.trip = :trip AND tm.member = :member")
    Status findSettlementStatusByTripAndMember(@Param("trip") Trip trip, @Param("member") Member member);
}
