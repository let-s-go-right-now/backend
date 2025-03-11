package com.lets.go.right.now.domain.trip.entity;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.settlement.entity.PersonalSpending;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    List<PersonalSpending> personalSpendings = new ArrayList<>();

    // 방장을 새로운 멤버로 업데이트
    public void changeOwner(Member newOwner) {
        this.owner = newOwner;
    }

    // 해당 여행의 모든 지출 내역 가져오기
    @Builder.Default
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Expense> expenses = new ArrayList<>(); // 여행과 관련된 지출 내역 추가

}
