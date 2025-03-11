package com.lets.go.right.now.domain.scrap.entity;

import com.lets.go.right.now.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scrapped_trip") // DB 테이블과 매핑
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ScrappedTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id; // 스크랩 ID (기본 키)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 스크랩한 회원 (FK)

    private String title; // 여행 제목

    @Column(name = "start_date")
    private String startDate; // 여행 시작일

    @Column(name = "end_date")
    private String endDate; // 여행 종료일

    private int budget; // 여행 예산

    @Column(name = "transport_mode")
    private String transportMode; // 교통 수단

    @Column(name = "scrapped_at", nullable = false, updatable = false)
    private LocalDateTime scrappedAt; // 스크랩 생성 시간

    @OneToOne(mappedBy = "scrappedTrip", cascade = CascadeType.ALL, orphanRemoval = true)
    private ScrappedTripDetail scrappedTripDetail;

    public ScrappedTrip(Member member, String title, String startDate, String endDate, Integer budget, String transportMode) {
        this.member = member;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budget = budget;
        this.transportMode = transportMode;
        this.scrappedAt = LocalDateTime.now();
    }

    // ScrappedTripDetail 설정 메서드 추가 (연관관계 편의 메서드)
    public void setScrappedTripDetail(ScrappedTripDetail scrappedTripDetail) {
        this.scrappedTripDetail = scrappedTripDetail;
        scrappedTripDetail.setScrappedTrip(this);
    }

}