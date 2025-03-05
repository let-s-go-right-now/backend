package com.lets.go.right.now.domain.chatgpt.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lets.go.right.now.domain.chatgpt.dto.ChatGptReq;
import com.lets.go.right.now.domain.chatgpt.dto.ChatGptRes;
import com.lets.go.right.now.domain.chatgpt.template.PromptTemplate;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatGptService {

    private final OpenAiChatModel openAiChatModel;
    private final ObjectMapper objectMapper;
    private final GooglePlacesService googlePlacesService;
    private final PromptTemplate promptTemplate;

    public ChatGptService(OpenAiChatModel openAiChatModel, ObjectMapper objectMapper,
                          GooglePlacesService googlePlacesService, PromptTemplate promptTemplate) {
        this.openAiChatModel = openAiChatModel;
        this.objectMapper = objectMapper;
        this.googlePlacesService = googlePlacesService;
        this.promptTemplate = promptTemplate;
    }

    public List<ChatGptRes> getTripRecommendationsFromGPT(ChatGptReq chatGptReq) {
        String promptText = promptTemplate.createPrompt(chatGptReq);

        // GPT 요청
        Prompt prompt = new Prompt(promptText);
        ChatResponse response = openAiChatModel.call(prompt);

        // string 타입의 응답 데이터를 json 타입으로 변환
        AssistantMessage jsonRes = response.getResult().getOutput();
        String jsonResponse = jsonRes.getText();

        // 빈 문자열을 null로 처리하도록 설정
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        try {
            // JSON 문자열을 DTO 객체로 변환
            List<ChatGptRes> travelList = objectMapper.readValue(
                    jsonResponse.trim().replaceAll("^```json|```$", "").trim(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, ChatGptRes.class)
            );

            return travelList.stream()
                    .map(travel -> {
                        try {
                            // photoUrl 비동기 실행으로 가져오기
                            return googlePlacesService.getPhotoNameFromTextSearch(travel.getPlace())
                                    .flatMap(googlePlacesService::getPhotoUrl)  // photoUrl 가져오기
                                    .flatMap(photoUrl -> {

                                        // 응답 DTO에 photoUrl 추가
                                        ChatGptRes responseDto = new ChatGptRes();
                                        responseDto.setTripImage(photoUrl);  // photoUrl을 tripImage에 설정
                                        responseDto.setTitle(travel.getTitle());
                                        responseDto.setPlace(travel.getPlace());
                                        responseDto.setDescription(travel.getDescription());
                                        responseDto.setTransportation(travel.getTransportation());
                                        responseDto.setCost(travel.getCost());
                                        responseDto.setItinerary(travel.getItinerary());

                                        // photoUrl을 설정한 responseDto 반환
                                        return Mono.just(responseDto);
                                    })
                                    .doOnTerminate(() -> {
                                        // 후속 작업이 끝났을 때 처리
                                        System.out.println("사진 URL 처리 완료!");
                                    })
                                    .onErrorResume(e -> {
                                        // 예외 처리
                                        e.printStackTrace();

                                        // 실패 시에도 DTO를 반환하여 계속 진행
                                        ChatGptRes fallbackDto = new ChatGptRes();
                                        fallbackDto.setTitle(travel.getTitle());
                                        fallbackDto.setPlace(travel.getPlace());
                                        fallbackDto.setDescription(travel.getDescription());
                                        fallbackDto.setTransportation(travel.getTransportation());
                                        fallbackDto.setCost(travel.getCost());
                                        fallbackDto.setItinerary(travel.getItinerary());
                                        return Mono.just(fallbackDto);  // 예외가 발생하면 기본값을 반환
                                    })
                                    .block(); // 결과를 기다림 (동기화)
                        } catch (Exception e) {
                            throw new RuntimeException("이미지 처리 실패: " + e.getMessage());
                        }
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 중 오류 발생: " + e.getMessage());
        }
    }
}
