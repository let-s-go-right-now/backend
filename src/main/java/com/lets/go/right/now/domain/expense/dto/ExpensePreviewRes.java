package com.lets.go.right.now.domain.expense.dto;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.enums.Category;
import java.time.LocalDateTime;

public record ExpensePreviewRes(
        String expenseName,
        Integer price,
        Category category,
        LocalDateTime expenseTime
) {
    public static ExpensePreviewRes of(Expense expense) {
        return new ExpensePreviewRes(
                expense.getExpenseName(),
                expense.getPrice(),
                expense.getCategory(),
                expense.getExpenseDate());
    }
}
