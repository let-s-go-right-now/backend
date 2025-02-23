package com.lets.go.right.now.domain.trip.entity;

import com.lets.go.right.now.domain.expense.entity.SettlementResult;
import com.lets.go.right.now.domain.member.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Trip")
public class Trip {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long id;
    private String name;
    private String introduce;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner; // 여행 방장

    // == 연관 관계 설정 == //
    // 여행에 참여중인 회원들
    @Builder.Default
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    List<TripMember> memberList = new ArrayList<>();

    // 정산 결과들
    @Builder.Default
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    List<SettlementResult> settlementResults = new ArrayList<>();
}
