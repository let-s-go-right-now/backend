package com.lets.go.right.now.domain.scrap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "scrapped_trip_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrappedTripDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_detail_id")
    private Long id; // 스크랩 상세 ID (기본 키)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_id", nullable = false, unique = true)
    private ScrappedTrip scrappedTrip; // 해당 스크랩 (FK)

    @Column(name = "departure")
    private String departure; // 출발지

    @Column(name = "transportation")
    private String transportation; // 이동 수단

    // 설명
    @Column(name = "itinerary", columnDefinition = "JSON")
    private String itinerary; // 여행 상세 일정

    public ScrappedTripDetail(ScrappedTrip scrappedTrip, String departure, String transportation, String itinerary) {
        this.scrappedTrip = scrappedTrip;
        this.departure = departure;
        this.transportation = transportation;
        this.itinerary = itinerary;
    }

    // ScrappedTrip 설정 메서드 추가 (연관관계 편의 메서드)
    public void setScrappedTrip(ScrappedTrip scrappedTrip) {
        this.scrappedTrip = scrappedTrip;
    }
}
