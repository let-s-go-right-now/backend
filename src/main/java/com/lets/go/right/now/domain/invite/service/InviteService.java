package com.lets.go.right.now.domain.invite.service;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.domain.tripMember.repository.TripMemberRepository;
import com.lets.go.right.now.global.enums.Status;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.enums.statuscode.SuccessStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class InviteService {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @Value("${invite.base-url}")
    private String baseUrl;

    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 1일

    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripMemberRepository tripMemberRepository;

    public InviteService(TripRepository tripRepository, MemberRepository memberRepository, TripMemberRepository tripMemberRepository) {
        this.tripRepository = tripRepository;
        this.memberRepository = memberRepository;
        this.tripMemberRepository = tripMemberRepository;
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

    // 초대 토큰을 통한 여행 가입
    public String joinTripWithInvite(String token, Long memberId) {

        // 1. 토큰을 통해 여행 ID 추출
        Long tripId;
        try {
            tripId = Long.parseLong(Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject());
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.TOKEN_ERROR);
        }

        // 2. 여행 정보 조회
        Optional<Trip> tripOptional = tripRepository.findById(tripId);
        if (tripOptional.isEmpty()) {
            throw new GeneralException(ErrorStatus._TRIP_NOT_FOUND);
        }
        Trip trip = tripOptional.get();

        // 3. 해당 여행에 이미 등록된 멤버인지 확인
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (memberOptional.isEmpty()) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }
        Member member = memberOptional.get();

        boolean isAlreadyMember = trip.getMemberList().stream()
                .anyMatch(m -> m.getMember().getId().equals(member.getId()));
        if (isAlreadyMember) {
            throw new GeneralException(ErrorStatus._MEMBER_IS_EXISTS);
        }

        // 4. 해당 여행에 멤버로 등록
        TripMember tripMember = TripMember.builder()
                .member(member)  // 로그인된 멤버
                .trip(trip)      // 여행
                .settlementStatus(Status.PROGRESS) // 초기 상태 : 정산 진행 중
                .build();

        trip.getMemberList().add(tripMember);
        tripMemberRepository.save(tripMember);
        tripRepository.save(trip);

        return SuccessStatus._OK.getMessage(); // 성공 메시지 반환
    }
}