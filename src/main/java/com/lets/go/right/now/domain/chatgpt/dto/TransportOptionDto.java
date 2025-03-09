package com.lets.go.right.now.domain.chatgpt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransportOptionDto {
    private String mode;
    private String duration;
    private Integer cost;
}
