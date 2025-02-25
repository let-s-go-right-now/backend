package com.lets.go.right.now.domain.expense.repository;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.SettlementResult;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementResultRepository extends JpaRepository<SettlementResult,Long> {
    List<SettlementResult> findByExpense(Expense expense);
}
