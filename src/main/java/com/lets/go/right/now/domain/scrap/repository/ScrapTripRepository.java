package com.lets.go.right.now.domain.scrap.repository;

import com.lets.go.right.now.domain.scrap.entity.ScrappedTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapTripRepository extends JpaRepository<ScrappedTrip, Long> {
}

