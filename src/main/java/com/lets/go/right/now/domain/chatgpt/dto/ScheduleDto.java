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
public class ScheduleDto {
    private String time;
    private String departure;
    private List<EventDto> events;
    private String destination;
}
