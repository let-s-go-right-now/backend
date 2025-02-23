package com.lets.go.right.now.domain.expense.dto;

import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.entity.enums.Category;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

class ExpenseCreateReqTest {

    @Test
    void 정상적인_카테고리_변환_테스트() {
        ExpenseCreateReq req = new ExpenseCreateReq("저녁 식사", 20000, "맛있는 스테이크",
                LocalDate.now(), "MEALS", "test@example.com", List.of());

        Expense expense = ExpenseCreateReq.of(req, Trip.builder().build(), Member.builder().build());

        assertEquals(Category.MEALS, expense.getCategory());
    }

    @Test
    void 잘못된_카테고리_입력시_예외발생() {
        ExpenseCreateReq req = new ExpenseCreateReq("기타 지출", 5000, "예상치 못한 비용",
                LocalDate.now(), "INVALID_CATEGORY", "test@example.com", List.of());

        GeneralException exception = assertThrows(GeneralException.class, () -> {
            ExpenseCreateReq.of(req, Trip.builder().build(), Member.builder().build());
        });

        // 에러 코드 검증
        assertEquals(ErrorStatus._CATEGORY_NOT_FOUND.getCode(), exception.getErrorCode());
    }
}
