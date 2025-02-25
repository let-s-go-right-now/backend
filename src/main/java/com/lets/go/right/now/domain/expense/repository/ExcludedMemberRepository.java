package com.lets.go.right.now.domain.expense.repository;

import com.lets.go.right.now.domain.expense.entity.ExcludedMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExcludedMemberRepository extends JpaRepository<ExcludedMember,Long> {
    @Modifying
    @Query("DELETE FROM ExcludedMember e WHERE e.expense.id = :expenseId")
    void deleteByExpenseId(@Param("expenseId") Long expenseId);
}
