package com.lets.go.right.now.domain.trip.repository;

import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripMemberRepository extends JpaRepository<TripMember, Long> {
    List<TripMember> findByTrip(Trip trip);
}
