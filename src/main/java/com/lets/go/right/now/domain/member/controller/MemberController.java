package com.lets.go.right.now.domain.member.controller;

import com.lets.go.right.now.domain.member.dto.JoinReq;
import com.lets.go.right.now.domain.member.dto.LoinReq;
import com.lets.go.right.now.domain.member.service.MemberService;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /**
     * 회원 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoinReq loinReq) {
        return memberService.login(loinReq);
    }

    /**
     * 회원 가입
     */
    @PostMapping("/join")
    public ResponseEntity<?> join(
            @RequestParam(value = "image", required = false) MultipartFile image,
            @Valid @ModelAttribute JoinReq joinReq) throws IOException {
        return memberService.join(joinReq, image);
    }
}
