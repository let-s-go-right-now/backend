package com.lets.go.right.now.domain.trip.dto;

public class TripDetailResponse {
    private Long tripId;
    private String name;
    private String introduce;
    private String startDate;
    private String endDate;

    // 추가 정보가 필요하면 필드 추가 (예: 방장, 참여자, 이미지 등)

    // Getters and Setters
    public Long getTripId() {
        return tripId;
    }
    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getIntroduce() {
        return introduce;
    }
    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }
    public String getStartDate() {
        return startDate;
    }
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
