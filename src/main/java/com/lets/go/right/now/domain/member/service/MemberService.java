package com.lets.go.right.now.domain.member.service;

import com.lets.go.right.now.domain.member.dto.JoinReq;
import com.lets.go.right.now.domain.member.dto.LoginRequestDTO;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.jwt.util.JwtUtil;
import com.lets.go.right.now.global.response.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 로그인
    @Transactional
    public ResponseEntity<?> login(LoginRequestDTO dto) {
        String email = dto.getEmail();
        String password = dto.getPassword();
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 비밀번호 검증
        if(!passwordEncoder.matches(password, member.getPassword())) {
            throw new GeneralException(ErrorStatus.PASSWORD_NOT_CORRECT);
        }

        String accessToken = jwtUtil.createJwt(member.getEmail(), member.getRole());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); // JWT 발급 성공시 Header에 삽입하여 반환

        return ResponseEntity.ok().headers(headers)
                .body(ApiResponse.onSuccess("Bearer " + accessToken));
    }

    /**
     * 회원 가입
     */
    public ResponseEntity<?> join(JoinReq joinReq) {
        // 동일 사용자 생성 방지
        if (memberRepository.existsMemberByEmail(joinReq.email())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.onFailure(ErrorStatus._MEMBER_IS_EXISTS, "회원가입에 실패하였습니다."));
        }

        // 새로운 회원 생성
        Member member = Member.toEntity(joinReq, passwordEncoder);
        memberRepository.save(member);

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess("회원 가입에 성공 하였습니다."));
    }
}
