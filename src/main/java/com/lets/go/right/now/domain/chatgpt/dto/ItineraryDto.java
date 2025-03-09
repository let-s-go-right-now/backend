package com.lets.go.right.now.domain.chatgpt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItineraryDto {
    private String day;
    private String date;
    private String title;
    private List<ScheduleDto> schedule;
    private String destination;
}
