package com.lets.go.right.now.domain.expense.repository;

import com.lets.go.right.now.domain.expense.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {
}
