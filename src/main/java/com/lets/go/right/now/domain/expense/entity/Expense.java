package com.lets.go.right.now.domain.expense.entity;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.global.entity.BaseEntity;
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
@Table(name = "Expense")
public class Expense extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private Long id;
    private String expenseName; // 지출 이름
    private Integer price; // 지출 금액
    private String details; // 상세 내역
    private LocalDate expenseDate; // 지출 날짜

    // == 연관 관계 설정 == //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payer_id")
    private Member payer; // 결제자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;


    // 지출과 관련된 여행 사진
    @Builder.Default
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<TripImage> tripImages = new ArrayList<>();

    // 지출에 제외된 사람들
    @Builder.Default
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<ExcludedMember> excludedMemberList = new ArrayList<>();

    // == 편의 메소드 ==
    public void changePayer(Member payer) {
        this.payer = payer;
    }
}
