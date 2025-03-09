package com.lets.go.right.now.domain.scrap.repository;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.scrap.entity.ScrappedTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScrapTripRepository extends JpaRepository<ScrappedTrip, Long> {
    List<ScrappedTrip> findByMember(Member member);
}

