package com.lets.go.right.now.domain.expense.service;

import com.lets.go.right.now.domain.expense.dto.ExpenseCreateReq;
import com.lets.go.right.now.domain.expense.entity.ExcludedMember;
import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.SettlementResult;
import com.lets.go.right.now.domain.expense.repository.ExcludedMemberRepository;
import com.lets.go.right.now.domain.expense.repository.ExpenseRepository;
import com.lets.go.right.now.domain.expense.repository.SettlementResultRepository;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.trip.repository.TripMemberRepository;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ExpenseServiceImplTest {

    @Autowired
    private ExpenseServiceImpl expenseService;
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private SettlementResultRepository settlementResultRepository;
    @Autowired
    private ExcludedMemberRepository excludedMemberRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TripMemberRepository tripMemberRepository;

    @Test
    void 지출_기록_정상_생성_및_정산결과_검증_테스트() throws IOException {
        // Given: 테스트용 회원 3명 생성 (결제자 포함)
        Member payer = createMember("payer@example.com", "결제자");
        Member member1 = createMember("user1@example.com", "참여자1"); // 제외될 회원
        Member member2 = createMember("user2@example.com", "참여자2");

        // 여행 생성 및 멤버 연관
        Trip trip = createTripWithMembers(List.of(payer, member1, member2));

        // 지출 생성 요청 (참여자1 제외)
        ExpenseCreateReq request = new ExpenseCreateReq(
                "식비", 1003, "정산 테스트",
                LocalDate.now(), "MEALS", "payer@example.com", List.of("user1@example.com")
        );

        // When: 지출 생성 API 실행
        expenseService.createExpense(trip.getId(), request, null);

        // Then: 지출이 정상적으로 저장되었는지 확인
        List<Expense> expenses = expenseRepository.findAll();
        assertThat(expenses).hasSize(1);
        assertThat(expenses.get(0).getExpenseName()).isEqualTo("식비");

        // ✅ ExcludedMember(지출 제외자) 저장 검증
        List<ExcludedMember> excludedMembers = excludedMemberRepository.findAll();
        assertThat(excludedMembers).hasSize(1);
        assertThat(excludedMembers.get(0).getExcludedMember().getEmail()).isEqualTo("user1@example.com");

        // ✅ SettlementResult(정산 결과) 저장 검증
        List<SettlementResult> settlements = settlementResultRepository.findAll();
        assertThat(settlements).hasSize(2); // 결제자 + 참여자2

        // 정산 금액 검증 (총 1003원을 결제자와 참여자2가 부담)
        SettlementResult settlementPayer = settlements.stream()
                .filter(s -> s.getSender().equals(payer))
                .findFirst()
                .orElseThrow();

        SettlementResult settlementMember2 = settlements.stream()
                .filter(s -> s.getSender().equals(member2))
                .findFirst()
                .orElseThrow();

        assertThat(settlementPayer.getAmount()).isEqualTo(502);
        assertThat(settlementMember2.getAmount()).isEqualTo(501);
    }

    /**
     * 테스트용 회원 생성
     */
    private Member createMember(String email, String name) {
        Member member = Member.builder()
                .email(email)
                .name(name)
                .build();
        return memberRepository.save(member);
    }

    /**
     * 여행 생성 및 멤버 추가
     */
    private Trip createTripWithMembers(List<Member> members) {
        Trip trip = tripRepository.save(Trip.builder().build());

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
