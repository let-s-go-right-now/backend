package com.lets.go.right.now.domain.invite.service;

import com.lets.go.right.now.domain.invite.dto.InviteLinkRes;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.response.ApiResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class InviteService {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${invite.base-url}")  // 초대 링크의 기본 URL
    private String baseUrl;

    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;

    public InviteService(TripRepository tripRepository, MemberRepository memberRepository) {
        this.tripRepository = tripRepository;
        this.memberRepository = memberRepository;
    }

    // 초대 링크 생성
    public ResponseEntity<?> generateInviteLink(String email, Long tripId) {

        // 1. 현재 로그인한 회원 조회
        Member loggedInMember = memberRepository.getMemberByEmail(email);

        // 2. 해당 여행 정보 가져오기
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));

        // 3. 초대할 권한이 있는지 확인 (해당 여행의 방장인지 체크)
        if (!trip.getOwner().getId().equals(loggedInMember.getId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 4. 랜덤 UUID 생성 (매번 새로운 값)
        String uniqueId = UUID.randomUUID().toString();

        // 5. 초대 링크에 사용할 JWT 토큰 생성
        String token = Jwts.builder()
                .setSubject(String.valueOf(tripId))  // 해당 여행의 ID
                .claim("uuid", uniqueId)  // 매번 다른 UUID 추가
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256) // 서명
                .compact();

        // 6. 초대 링크 생성
        String inviteLink = baseUrl + "?token=" + token;

        return ResponseEntity.ok(ApiResponse.onSuccess(InviteLinkRes.of(inviteLink)));
    }
}
