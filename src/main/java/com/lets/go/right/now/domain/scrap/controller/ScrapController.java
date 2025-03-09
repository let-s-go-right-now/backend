package com.lets.go.right.now.domain.scrap.controller;

import com.lets.go.right.now.domain.scrap.dto.ScrapTripReq;
import com.lets.go.right.now.domain.scrap.service.ScrapService;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    // 마이페이지에서 회원이 스크랩한 여행 목록 조회
    @GetMapping("/api/mypage/scrap/list")
    public ResponseEntity<?> getScrappedTrips(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return scrapService.getScrappedTrips(customUserDetails.getEmail());
    }

    // 회원이 스크랩한 여행 상세 일정 조회
    @GetMapping("api/mypage/scrap/{scrap_id}")
    public ResponseEntity<?> getScrappedTripsDetail(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PathVariable("scrap_id") Long scrapId) {
        return scrapService.getScrappedTripsDetail(customUserDetails.getEmail(), scrapId);
    }

}


