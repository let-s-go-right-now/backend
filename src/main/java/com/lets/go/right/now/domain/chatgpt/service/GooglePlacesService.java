package com.lets.go.right.now.domain.chatgpt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GooglePlacesService {

    @Value("${google.places.api-key}")
    private String googleApiKey;

    @Value("${google.places.api.url.search}")
    private String SearchUrl;

    @Value("${google.places.api.url.photo}")
    private String photoUrl;

    private final WebClient webClient;

    public GooglePlacesService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 1. 텍스트 검색 API를 호출하여 place의 photo.name 가져오기
     */
    public Mono<String> getPhotoNameFromTextSearch(String query) {
        return webClient.post()
                .uri(SearchUrl)
                .header("Content-Type", "application/json")
                .header("X-Goog-Api-Key", googleApiKey)
                .header("X-Goog-FieldMask", "places.id,places.photos") // id도 함께 요청
                .bodyValue("{\"textQuery\":\"" + query + "\"}")
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    String photoName = extractPhotoName(response);
                    return photoName != null ? Mono.just(photoName) : Mono.error(new RuntimeException("No photo found"));
                });
    }

    /**
     * JSON 응답에서 photo.name 추출
     */
    private String extractPhotoName(String jsonString) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            JsonNode placesNode = rootNode.path("places");
            if (placesNode.isArray() && placesNode.size() > 0) {
                JsonNode photosNode = placesNode.get(0).path("photos");
                if (photosNode.isArray() && photosNode.size() > 0) {
                    return photosNode.get(0).path("name").asText(); // 첫 번째 사진 name 반환
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON for photo name: " + e.getMessage());
        }
        return null;
    }

    /**
     * 2. 장소 사진 API 호출하여 실제 사진 URL 반환
     */
    public Mono<String> getPhotoUrl(String photoName) {

        if (photoName == null || photoName.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Invalid photo name"));
        }

        // 사진 URL을 생성할 때 max_height_px 및 max_width_px를 추가
        String fullPhotoUrl = photoUrl + photoName + "/media?key=" + googleApiKey
                + "&max_height_px=600"  // 최대 높이를 600px로 설정
                + "&max_width_px=600";   // 최대 너비를 600px로 설정

        return Mono.just(fullPhotoUrl);
    }

}
