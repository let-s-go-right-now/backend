package com.lets.go.right.now.domain.expense.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.security.PublicKey;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Trip_Image")
public class TripImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_image_id")
    private Long id;

    private String imageUrl;

    // == 연관 관계 설정 == //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id")
    private Expense expense;

    // == 편의 메소드 == //
    public static TripImage toEntity(String imageUrl, Expense expense) {
        return TripImage.builder()
                .expense(expense)
                .imageUrl(imageUrl)
                .build();
    }

    public void changeExpense(Expense expense) {
        this.expense = expense;
    }
}
