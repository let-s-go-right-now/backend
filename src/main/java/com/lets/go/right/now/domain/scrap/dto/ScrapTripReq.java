package com.lets.go.right.now.domain.scrap.dto;

import com.lets.go.right.now.domain.chatgpt.dto.ItineraryDto;
import lombok.*;

import java.util.List;

// 클라이언트가 입력한 ChatGptReqDto + ChatGPT가 응답한 ChatGptResDto = ScrapTripReqDto

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapTripReq {

    // 사용자 입력 (ChatGptReqDto에서 필요한 필드만)
    private String startDate;
    private String endDate;
    private int budget;
    private String transportMode;
    private String departure;

    // gpt 응답 (ChatGptResDto에서 필요한 필드만)
    private String title;
    private String description;
    private String transportation;
    private int cost;
    private List<ItineraryDto> itinerary;

}
