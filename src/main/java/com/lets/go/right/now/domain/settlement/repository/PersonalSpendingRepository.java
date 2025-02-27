package com.lets.go.right.now.domain.settlement.repository;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.settlement.entity.PersonalSpending;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonalSpendingRepository extends JpaRepository<PersonalSpending, Long> {
    List<PersonalSpending> findByExpense(Expense expense);

    @Modifying
    @Query("DELETE FROM PersonalSpending ps WHERE ps.expense.id = :expenseId")
    void deleteByExpenseId(@Param("expenseId") Long expenseId);

    @Query("SELECT ps FROM PersonalSpending ps "
            + "WHERE ps.trip.id = :tripId "
            + "AND ps.sender.id = :senderId "
            + "AND ps.expense IS NOT NULL")
    Page<PersonalSpending> findMyPersonalSpending(
            @Param("tripId") Long tripId, @Param("senderId") Long senderId, Pageable pageable);
}
