package com.lets.go.right.now.domain.scrap.controller;

import com.lets.go.right.now.domain.scrap.dto.ScrapTripReq;
import com.lets.go.right.now.domain.scrap.service.ScrapService;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 스크랩 관련 API 컨트롤러
 */

@RestController
@RequiredArgsConstructor
public class ScrapController {

    private final ScrapService scrapService;

    // 회원이 스크랩 생성
    @PostMapping("/api/scrap")
    public ResponseEntity<?> createScrap(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody ScrapTripReq scrapTripReq) {
        return scrapService.createScrap(customUserDetails.getEmail(), scrapTripReq);
    }

}


