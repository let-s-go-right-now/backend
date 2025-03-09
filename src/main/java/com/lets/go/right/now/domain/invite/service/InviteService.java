package com.lets.go.right.now.domain.invite.service;

import com.lets.go.right.now.domain.invite.dto.InviteLinkRes;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.domain.tripMember.enums.Status;
import com.lets.go.right.now.domain.tripMember.repository.TripMemberRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class InviteService {

    @Value("${spring.jwt.secret}")
    private String secretKey; // JWT 서명에 사용할 비밀 키

    @Value("${invite.base-url}")  // 초대 링크의 기본 URL
    private String baseUrl;

    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 토큰 만료 시간 (1일)

    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripMemberRepository tripMemberRepository;

    public InviteService(TripRepository tripRepository, MemberRepository memberRepository, TripMemberRepository tripMemberRepository) {
        this.tripRepository = tripRepository;
        this.memberRepository = memberRepository;
        this.tripMemberRepository = tripMemberRepository;
    }

    // 초대 링크 생성
    public ResponseEntity<?> createInviteLink(String email, Long tripId) {

        // 1. 현재 로그인한 회원 조회
        Member inviter = memberRepository.getMemberByEmail(email);

        // 2. 여행 정보 가져오기
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));

        // 3. 초대 권한 확인 (여행의 방장인지 체크)
        if (!trip.getOwner().getId().equals(inviter.getId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        // 4. 초대 링크에 사용할 JWT 토큰 생성
        String token = Jwts.builder()
                .setSubject(String.valueOf(tripId)) // 여행 ID 저장
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간 설정
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256) // 서명 생성
                .compact();

        // 5. 초대 링크 생성
        String inviteLink = baseUrl + "?token=" + token;

        return ResponseEntity.ok(ApiResponse.onSuccess(InviteLinkRes.of(inviteLink))); // 초대 링크 반환
    }

    // 초대 링크를 통한 여행 멤버 등록
    public ResponseEntity<?> joinWithInviteLink(String email, String token) {

        // 1. 초대 링크를 통해 로그인한 회원 조회
        Member invitedMember = memberRepository.getMemberByEmail(email);
        if (invitedMember == null) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }

        // 2. 토큰을 통해 여행 ID 추출
        Long tripId;
        try {
            tripId = Long.parseLong(Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8))) // 서명 검증
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject()); // 토큰의 subject에서 여행 ID 가져오기
        } catch (ExpiredJwtException e) {
            throw new GeneralException(ErrorStatus.TOKEN_ERROR); // 토큰 만료 예외 처리
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.TOKEN_ERROR); // 기타 토큰 오류 처리
        }

        // 3. 여행 정보 조회
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._TRIP_NOT_FOUND));

        // 4. 이미 여행에 등록된 멤버인지 확인
        boolean isAlreadyMember = trip.getMemberList().stream()
                .anyMatch(m -> m.getMember().getId().equals(invitedMember.getId()));
        if (isAlreadyMember) {
            throw new GeneralException(ErrorStatus._MEMBER_IS_EXISTS);
        }

        // 5. 여행 멤버로 등록
        TripMember tripMember = TripMember.builder()
                .member(invitedMember)  // 초대된 멤버
                .trip(trip)      // 여행 정보
                .settlementStatus(Status.PROGRESS) // 초기 상태: 정산 진행 중
                .build();

        trip.getMemberList().add(tripMember); // 여행 멤버 리스트에 추가
        tripMemberRepository.save(tripMember); // DB에 저장
        tripRepository.save(trip); // 여행 정보 업데이트

        return ResponseEntity.ok(ApiResponse.onSuccess("해당 여행의 멤버로 등록되었습니다."));
    }
}
