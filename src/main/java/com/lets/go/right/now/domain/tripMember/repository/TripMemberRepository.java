package com.lets.go.right.now.domain.tripMember.repository;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.ErrorResponseException;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
    List<TripMember> findByTrip(Trip trip);

    default TripMember getByTripAndMember(Trip trip, Member member) {
        return findByTripAndMember(trip, member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));
    }

    Optional<TripMember> findByTripAndMember(Trip trip, Member member);

    List<TripMember> findByTripId(Long tripId);
}
