package com.lets.go.right.now.domain.settlement.entity;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

/**
 * <개인별 지출>
 * 지출을 개인별 지출로 분리
 * -> 어떤 여행에서 누구에게 얼만큼 보내야 하는 지
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "personal_spending")
public class PersonalSpending extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personal_spending_id")
    private Long id;
    private Integer amount;

    // == 연관 관계 설정 == //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id")
    private Expense expense;

    // == 편의 메소드 == //
    public static PersonalSpending toEntity(
            Trip trip, Expense expense, Integer amount, Member sender, Member receiver) {
        return PersonalSpending.builder()
                .trip(trip)
                .expense(expense)
                .amount(amount)
                .sender(sender)
                .receiver(receiver)
                .build();
    }
}
