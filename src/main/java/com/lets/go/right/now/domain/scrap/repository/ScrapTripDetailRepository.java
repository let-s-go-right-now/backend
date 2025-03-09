package com.lets.go.right.now.domain.scrap.repository;

import com.lets.go.right.now.domain.scrap.entity.ScrappedTrip;
import com.lets.go.right.now.domain.scrap.entity.ScrappedTripDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ScrapTripDetailRepository extends JpaRepository<ScrappedTripDetail, Long> {
    // ScrappedTrip 기반으로 ScrappedTripDetail 찾기
    Optional<ScrappedTripDetail> findByScrappedTrip(ScrappedTrip scrappedTrip);
}