package com.lets.go.right.now.domain.member.service;

import com.lets.go.right.now.domain.member.dto.JoinReq;
import com.lets.go.right.now.domain.member.dto.LoinReq;
import org.springframework.http.ResponseEntity;

public interface MemberService {
    ResponseEntity<?> login(LoinReq dto);
    ResponseEntity<?> join(JoinReq joinReq);
}
