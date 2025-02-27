package com.lets.go.right.now.domain.settlement.entity;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.global.entity.BaseEntity;
import com.lets.go.right.now.global.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <여행 정산>
 * 여행에서 발생한 모든 지출의 정산 금액
 */
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "travel_settlement")
public class TravelSettlement extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "travel_settlement_id")
    private Long id;
    private Integer amount;
    @Enumerated(value = EnumType.STRING)
    private Status settlementStatus; // 정산 현황 PROGRESS, DONE

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

    // == 편의 메소드 == //
    public static TravelSettlement toEntity(
            Trip trip, Integer amount, Member sender, Member receiver) {
        return TravelSettlement.builder()
                .trip(trip)
                .amount(amount)
                .sender(sender)
                .receiver(receiver)
                .settlementStatus(Status.PROGRESS)
                .build();
    }
}
