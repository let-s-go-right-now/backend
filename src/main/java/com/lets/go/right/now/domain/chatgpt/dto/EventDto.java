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
public class EventDto {
    private String type;
    private String location;
    private String from;
    private String to;
    private List<TransportOptionDto> options;
    private String details;
    private Integer cost;
    private String link;
    private List<String> hashtags;
}
