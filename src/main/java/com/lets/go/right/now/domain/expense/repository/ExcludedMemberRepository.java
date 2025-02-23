package com.lets.go.right.now.domain.expense.repository;

import com.lets.go.right.now.domain.expense.entity.ExcludedMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExcludedMemberRepository extends JpaRepository<ExcludedMember,Long> {
}
