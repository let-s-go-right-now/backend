package com.lets.go.right.now.domain.chatgpt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// chat gpt가 응답한 데이터

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatGptRes {
    private String title;
    private String place;
    private String description;
    private String transportation;
    private int cost;
    private String tripImage;
    private List<ItineraryDto> itinerary;
}

