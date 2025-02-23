package com.lets.go.right.now.domain.expense.entity;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.service.MemberService;
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

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Excluded_Member")
public class ExcludedMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // == 연관 관계 설정 == //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "excluded_member_id")
    private Member excludedMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id")
    private Expense expense;

    // == 편의 메소드 == //
    public static ExcludedMember toEntity(Member excludedMember, Expense expense) {
        return ExcludedMember.builder()
                .excludedMember(excludedMember)
                .expense(expense)
                .build();
    }
}
