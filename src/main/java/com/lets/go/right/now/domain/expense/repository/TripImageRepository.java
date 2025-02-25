package com.lets.go.right.now.domain.expense.repository;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.TripImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TripImageRepository extends JpaRepository<TripImage,Long> {
    List<TripImage> findAllByExpense(Expense expense);
    @Modifying
    @Query("DELETE FROM TripImage t WHERE t.expense.id = :expenseId")
    void deleteByExpenseId(@Param("expenseId") Long expenseId);
}
