package com.lets.go.right.now.domain.scrap.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lets.go.right.now.domain.chatgpt.dto.ItineraryDto;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.scrap.dto.ScrapTripReq;
import com.lets.go.right.now.domain.scrap.dto.ScrapTripRes;
import com.lets.go.right.now.domain.scrap.entity.ScrappedTrip;
import com.lets.go.right.now.domain.scrap.entity.ScrappedTripDetail;
import com.lets.go.right.now.domain.scrap.repository.ScrapTripDetailRepository;
import com.lets.go.right.now.domain.scrap.repository.ScrapTripRepository;
import com.lets.go.right.now.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapTripRepository scrappedTripRepository;
    private final ScrapTripDetailRepository scrappedTripDetailRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper; // JSON 변환용

    @Autowired
    public ScrapService(ObjectMapper objectMapper, ScrapTripRepository scrappedTripRepository,
                        ScrapTripDetailRepository scrappedTripDetailRepository, MemberRepository memberRepository) {
        this.objectMapper = objectMapper;
        this.scrappedTripRepository = scrappedTripRepository;
        this.scrappedTripDetailRepository = scrappedTripDetailRepository;
        this.memberRepository = memberRepository;
    }

    public ResponseEntity<?> createScrap(String email, ScrapTripReq scrapTripReq) {

        // 이메일로 회원 조회
        Member member = memberRepository.getMemberByEmail(email);

        ScrappedTrip scrappedTrip = new ScrappedTrip(
                member,
                scrapTripReq.getTitle(),
                scrapTripReq.getStartDate(),
                scrapTripReq.getEndDate(),
                scrapTripReq.getBudget(),
                scrapTripReq.getTransportMode()
        );

        ScrappedTripDetail scrappedTripDetail = new ScrappedTripDetail(
                scrappedTrip,
                scrapTripReq.getDeparture(),
                scrapTripReq.getTransportation(),
                convertItineraryToJson(scrapTripReq.getItinerary()) // JSON 타입으로 변환
        );

        // ScrappedTrip 저장
        scrappedTripRepository.save(scrappedTrip);

        // ScrappedTripDetail 저장
        scrappedTripDetailRepository.save(scrappedTripDetail);

        return ResponseEntity.ok(ApiResponse.onSuccess("해당 여행지 정보가 스크랩 되었습니다."));
    }

    // DB에 itinerary를 JSON 형식으로 저장하기 위해 변환
    private String convertItineraryToJson(List<ItineraryDto> itinerary) {
        try {
            return objectMapper.writeValueAsString(itinerary);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("요청한 JSON 데이터 형식이 올바르지 않습니다.", e);
        }
    }

    public ResponseEntity<?> getScrappedTrips(String email) {
        // 이메일로 회원 조회
        Member member = memberRepository.getMemberByEmail(email);

        // 회원 스크랩한 여행 목록 조회
        List<ScrappedTrip> scrappedTrips = scrappedTripRepository.findByMember(member);

        // ScrappedTrip을 ScrapTripRes DTO로 변환
        List<ScrapTripRes> scrappedTripResList = scrappedTrips.stream()
                .map(ScrapTripRes::new) // ScrappedTrip -> ScrapTripRes 변환
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.onSuccess(scrappedTripResList));
    }

}



