package com.lets.go.right.now.domain.expense.repository;

import com.lets.go.right.now.domain.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
}
