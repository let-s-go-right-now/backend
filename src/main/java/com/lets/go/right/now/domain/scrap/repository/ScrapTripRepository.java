package com.lets.go.right.now.domain.scrap.repository;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.scrap.entity.ScrappedTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapTripRepository extends JpaRepository<ScrappedTrip, Long> {
    // 특정 회원이 저장한 모든 스크랩 목록을 조회
    List<ScrappedTrip> findByMember(Member member);

    // 특정 회원의 scrap_id를 조회
    Optional<ScrappedTrip> findByIdAndMember(Long scrapId, Member member);

}