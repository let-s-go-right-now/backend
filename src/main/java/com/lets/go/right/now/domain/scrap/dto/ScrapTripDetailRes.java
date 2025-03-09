package com.lets.go.right.now.domain.scrap.dto;

import com.lets.go.right.now.domain.scrap.entity.ScrappedTrip;
import com.lets.go.right.now.domain.scrap.entity.ScrappedTripDetail;

public record ScrapTripDetailRes(
        String title,
        String startDate,
        String endDate,
        int budget,
        String departure,
        String transportation,
        String itinerary
) {
    public ScrapTripDetailRes(ScrappedTrip scrappedTrip, ScrappedTripDetail scrappedTripDetail) {
        this(
                scrappedTrip.getTitle(),
                scrappedTrip.getStartDate(),
                scrappedTrip.getEndDate(),
                scrappedTrip.getBudget(),
                scrappedTripDetail.getDeparture(),
                scrappedTripDetail.getTransportation(),
                scrappedTripDetail.getItinerary()
        );
    }
}
