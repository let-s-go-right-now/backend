package com.lets.go.right.now.init;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.trip.repository.TripMemberRepository;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

//@Component
//@Order(1)
@RequiredArgsConstructor
public class TripDataLoader implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("📌 테스트 데이터를 로드합니다...");

        // ✅ 1. 테스트용 회원 생성
        Member payer1 = createMember("payer1@example.com", "결제자1", "payer1234");
        Member payer2 = createMember("payer2@example.com", "결제자2", "payer21234");
        Member payer3 = createMember("payer3@example.com", "결제자3", "payer31234");

        Member memberA = createMember("userA@example.com", "참여자A", "userA1234");
        Member memberB = createMember("userB@example.com", "참여자B", "userB1234");
        Member memberC = createMember("userC@example.com", "참여자C", "userC1234");
        Member memberD = createMember("userD@example.com", "참여자D", "userD1234");

        // ✅ 2. 여행 생성 및 멤버 연관 (여행 1: 제주도, 여행 2: 부산, 여행 3: 강릉)
        Trip trip1 = createTripWithMembers("제주도 여행", List.of(payer1, memberA, memberB, memberC));
        Trip trip2 = createTripWithMembers("부산 여행", List.of(payer2, memberA, memberB, memberD));
        Trip trip3 = createTripWithMembers("강릉 여행", List.of(payer3, memberA, memberC, memberD));

        System.out.println("✅ 테스트 데이터 로드 완료!");
        System.out.println("🔹 생성된 여행 목록:");
        System.out.println("    - 제주도 여행 ID: " + trip1.getId());
        System.out.println("    - 부산 여행 ID: " + trip2.getId());
        System.out.println("    - 강릉 여행 ID: " + trip3.getId());
        System.out.println("🔹 생성된 회원 목록: " +
                memberRepository.findAll().stream().map(Member::getEmail).collect(Collectors.toList()));
    }

    /**
     * 테스트용 회원을 생성하는 메서드
     */
    private Member createMember(String email, String name, String password) {
        return memberRepository.save(
                Member.builder()
                        .email(email)
                        .name(name)
                        .password(passwordEncoder.encode(password)) // 비밀번호 암호화 저장
                        .build()
        );
    }

    /**
     * 여행을 생성하고, 멤버들을 여행에 추가하는 메서드
     */
    private Trip createTripWithMembers(String tripName, List<Member> members) {
        Trip trip = tripRepository.save(
                Trip.builder()
                        .name(tripName)
                        .introduce("테스트 여행: " + tripName)
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(3))
                        .build()
        );

        List<TripMember> tripMembers = members.stream()
                .map(member -> TripMember.builder()
                        .member(member)
                        .trip(trip)
                        .build())
                .collect(Collectors.toList());

        tripMemberRepository.saveAll(tripMembers);

        return trip;
    }
}

