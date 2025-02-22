package com.lets.go.right.now.domain.member.service;

import com.lets.go.right.now.domain.member.dto.JoinReq;
import com.lets.go.right.now.domain.member.dto.LoinReq;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    ResponseEntity<?> login(LoinReq dto);
    ResponseEntity<?> join(JoinReq joinReq, MultipartFile image) throws IOException;
}
