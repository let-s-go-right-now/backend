package com.lets.go.right.now.domain.scrap.dto;

import com.lets.go.right.now.domain.scrap.entity.ScrappedTrip;
import java.time.LocalDateTime;

// 서버가 클라이언트에게 회원이 스크랩한 여행 정보 목록을 전달할 때 사용되는 DTO
public record ScrapTripRes(
        Long id,
        String title,
        String startDate,
        String endDate,
        int budget,
        String transportMode,
        LocalDateTime scrappedAt
) {
    public ScrapTripRes(ScrappedTrip scrappedTrip) {
        this(
                scrappedTrip.getId(),
                scrappedTrip.getTitle(),
                scrappedTrip.getStartDate(),
                scrappedTrip.getEndDate(),
                scrappedTrip.getBudget(),
                scrappedTrip.getTransportMode(),
                scrappedTrip.getScrappedAt()
        );
    }
}
