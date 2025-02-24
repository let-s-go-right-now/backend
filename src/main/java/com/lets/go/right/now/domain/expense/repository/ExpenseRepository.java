package com.lets.go.right.now.domain.expense.repository;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    default Expense getExpenseById(Long expenseId) {
        return findById(expenseId).orElseThrow(() -> new GeneralException(ErrorStatus._EXPENSE_NOT_FOUND));
    }
    Optional<Expense> findById(Long expenseId);
}
