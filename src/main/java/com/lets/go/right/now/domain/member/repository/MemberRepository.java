package com.lets.go.right.now.domain.member.repository;

import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Boolean existsMemberByEmail(String email);
    default Member getMemberByEmail(String email) {
        return findMemberByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }
    Optional<Member> findMemberByEmail(String email);
}
