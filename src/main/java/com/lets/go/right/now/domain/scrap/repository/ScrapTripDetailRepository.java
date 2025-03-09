package com.lets.go.right.now.domain.scrap.repository;

import com.lets.go.right.now.domain.scrap.entity.ScrappedTripDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapTripDetailRepository extends JpaRepository<ScrappedTripDetail, Long> {

}

