package com.lets.go.right.now.domain.expense.repository;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.SettlementResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettlementResultRepository extends JpaRepository<SettlementResult,Long> {
    List<SettlementResult> findByExpense(Expense expense);

    @Modifying
    @Query("DELETE FROM SettlementResult s WHERE s.expense.id = :expenseId")
    void deleteByExpenseId(@Param("expenseId") Long expenseId);

}
