package com.lets.go.right.now.domain.expense.repository;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.TripImage;
import java.util.List;

import com.lets.go.right.now.domain.trip.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TripImageRepository extends JpaRepository<TripImage,Long> {
    List<TripImage> findAllByExpense(Expense expense);
    @Modifying
    @Query("DELETE FROM TripImage t WHERE t.expense.id = :expenseId")
    void deleteByExpenseId(@Param("expenseId") Long expenseId);

    // 특정 여행(Trip)에 속한 모든 TripImage 조회 (Expense를 경유)
    @Query("SELECT ti FROM TripImage ti WHERE ti.expense.trip = :trip")
    List<TripImage> findAllByTrip(@Param("trip") Trip trip);
}
