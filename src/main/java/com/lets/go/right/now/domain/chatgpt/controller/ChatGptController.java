package com.lets.go.right.now.domain.chatgpt.controller;

import com.lets.go.right.now.domain.chatgpt.dto.ChatGptReq;
import com.lets.go.right.now.domain.chatgpt.dto.ChatGptRes;
import com.lets.go.right.now.domain.chatgpt.service.ChatGptService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatGptController {
    private final ChatGptService travelService;

    public ChatGptController(ChatGptService travelService) {
        this.travelService = travelService;
    }

    // chat gpt에게 여행지 추천 정보 받기
    @PostMapping("/api/trips/recommend")
    public List<ChatGptRes> getTripRecommendationsFromGPT(@RequestBody ChatGptReq request) {
        return travelService.getTripRecommendationsFromGPT(request);
    }

}
