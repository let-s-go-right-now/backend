package com.lets.go.right.now.domain.trip.entity;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.settlement.entity.PersonalSpending;
import com.lets.go.right.now.domain.settlement.entity.TravelSettlement;
import com.lets.go.right.now.global.entity.BaseEntity;
import com.lets.go.right.now.global.enums.Status;
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
public class Trip extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long id;
    private String name;
    private String introduce;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(value = EnumType.STRING)
    private Status tripStatus; // 여행 상태 추가

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

    // 여행 종료 메서드 추가
    public void endTrip() {
        this.tripStatus = Status.DONE;
    }

    // 여행이 종료되었는지 확인하는 메서드
    public boolean isTripEnded() {
        return this.tripStatus == Status.DONE;
    }

    // 여행 생성 편의 메서드
    public static Trip toEntity(String name, String introduce, LocalDate startDate, LocalDate endDate, Member owner) {
        return Trip.builder()
                .name(name)
                .introduce(introduce)
                .startDate(startDate)
                .endDate(endDate)
                .owner(owner)
                .tripStatus(Status.PROGRESS) // 기본값은 진행 중
                .build();
    }

    // 해당 여행의 모든 지출 내역 가져오기
    @Builder.Default
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<Expense> expenses = new ArrayList<>(); // 여행과 관련된 지출 내역 추가

    // 해당 여행의 정산 결과
    @Builder.Default
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<TravelSettlement> travelSettlements = new ArrayList<>();

}
