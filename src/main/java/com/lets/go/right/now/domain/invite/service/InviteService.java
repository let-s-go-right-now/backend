package com.lets.go.right.now.domain.invite.service;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class InviteService {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${invite.base-url}")
    private String baseUrl;

    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1일

    private final TripRepository tripRepository;

    public InviteService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    // 해당 여행에 초대할 권한이 있는지 확인
    public boolean isUserAuthorizedToInvite(Member loggedInMember, Long tripId) {

        // 1. 해당 여행 정보 가져오기
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));

        // 2. 해당 여행의 방장이면 true
        return trip.getOwner().getId().equals(loggedInMember.getId());

    }

    // 초대 링크 생성
    public String generateInviteLink(Long tripId) {

        // 1. 해당 여행 정보 가져오기
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));

        // 2. 토큰 생성
        String token = Jwts.builder()
                .setSubject(String.valueOf(tripId))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        return baseUrl + "?token=" + token;
    }
}
