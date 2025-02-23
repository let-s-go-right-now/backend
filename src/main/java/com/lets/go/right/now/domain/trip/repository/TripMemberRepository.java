package com.lets.go.right.now.domain.trip.repository;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
    ArrayList<Member> findTripMembersByTrip(Trip trip);
}
