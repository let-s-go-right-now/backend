package com.lets.go.right.now.domain.expense.repository;

import com.lets.go.right.now.domain.expense.entity.SettlementResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementResultRepository extends JpaRepository<SettlementResult,Long> {
}
