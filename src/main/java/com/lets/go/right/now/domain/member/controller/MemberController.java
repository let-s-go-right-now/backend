package com.lets.go.right.now.domain.member.controller;

import com.lets.go.right.now.domain.member.dto.*;
import com.lets.go.right.now.domain.member.service.MemberService;
import com.lets.go.right.now.global.jwt.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    /**
     * 계좌 번호 요청
     */
    @GetMapping("/account-number")
    public ResponseEntity<?> getAccountNumber(@RequestBody AccountReq accountReq) {
        return memberService.getAccountNumber(accountReq);
    }

    // 로그 아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 클라이언트에서 JWT 토큰을 삭제하도록 유도
        return ResponseEntity.ok(new LogoutRes("로그 아웃 완료"));
    }

    // 회원 탈퇴
    @PostMapping("/leave")
    public LeaveRes leave(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 사용자 정보를 기반으로 탈퇴 처리
        memberService.deleteMember(customUserDetails.getEmail());
        return new LeaveRes("회원 탈퇴 완료");
    }

    // 회원 조회
    @GetMapping("/info")
    public ResponseEntity<MemberInfoRes> info(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // CustomUserDetails에서 이메일을 가져옴
        String email = customUserDetails.getEmail();

        // 이메일로 회원 정보를 가져옴
        MemberInfoRes response = memberService.getMemberInfo(email);

        return ResponseEntity.ok(response);
    }
}
