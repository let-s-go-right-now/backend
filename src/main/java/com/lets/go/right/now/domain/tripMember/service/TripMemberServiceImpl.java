package com.lets.go.right.now.domain.tripMember.service;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.dto.TripDetailResponse;
import com.lets.go.right.now.domain.trip.dto.TripMemberListRes;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.global.enums.Status;
import com.lets.go.right.now.domain.tripMember.repository.TripMemberRepository;
import com.lets.go.right.now.global.response.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripMemberServiceImpl implements TripMemberService {
    private final TripRepository tripRepository;
    private final MemberRepository memberRepository;
    private final TripMemberRepository tripMemberRepository;

    @Override
    @Transactional
    public ResponseEntity<?> finishSettlement(String email, Long tripId) {
        Member member = memberRepository.getMemberByEmail(email);
        Trip trip = tripRepository.getTripById(tripId);
        // 일치하는 여행회원 정보 조회
        TripMember tripMember = tripMemberRepository.getByTripAndMember(trip, member);
        tripMember.changeStatus(Status.DONE); // 종료 상태로 변경
        return ResponseEntity.ok(ApiResponse.onSuccess("정산을 종료하였습니다."));
    }
}
