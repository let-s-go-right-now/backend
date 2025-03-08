package com.lets.go.right.now.domain.member.service;

import com.lets.go.right.now.domain.member.dto.*;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.jwt.util.JwtUtil;
import com.lets.go.right.now.global.response.ApiResponse;
import com.lets.go.right.now.global.s3.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final JwtUtil jwtUtil;

    /**
     * 로그인
     */
    @Transactional
    public ResponseEntity<?> login(LoinReq dto) {
        // 회원 검증
        Member member = memberRepository.getMemberByEmail(dto.email());
        // 비밀번호 검증
        if(!passwordEncoder.matches(dto.password(), member.getPassword())) {
            throw new GeneralException(ErrorStatus.PASSWORD_NOT_CORRECT);
        }

        String accessToken = jwtUtil.createJwt(member.getEmail(), member.getRole());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); // JWT 발급 성공시 Header에 삽입하여 반환

        return ResponseEntity.ok().headers(headers)
                .body(ApiResponse.onSuccess(LoginRes.of(member,accessToken)));
    }

    /**
     * 회원 가입
     */
    @Transactional
    public ResponseEntity<?> join(JoinReq joinReq, MultipartFile image) throws IOException {
        // 동일 사용자 생성 방지
        if (memberRepository.existsMemberByEmail(joinReq.email())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.onFailure(ErrorStatus._MEMBER_IS_EXISTS, "회원가입에 실패하였습니다."));
        }

        // 새로운 회원 생성
        Member member = Member.toEntity(joinReq, passwordEncoder);
        memberRepository.save(member);

        // 이미지가 존재하는 경우에만 이미지 업로드 및 설정
        if(image!=null && !image.isEmpty()){
            member.changeProfileImgLink(s3Service.uploadFile(image));
        }

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess("회원 가입에 성공 하였습니다."));
    }

    /**
     * 계좌 번호 얻어 오기
     */
    @Override
    public ResponseEntity<?> getAccountNumber(AccountReq accountReq) {
        Member member = memberRepository.getMemberByEmail(accountReq.userEmail());
        return ResponseEntity.ok(ApiResponse.onSuccess(member.getAccountNumber()));
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    @Override
    public ResponseEntity<?> deleteMember(String email) {
        // 이메일을 통해 사용자 조회 후 삭제
        memberRepository.deleteByEmail(email);
        return ResponseEntity.ok(ApiResponse.onSuccess("회원 탈퇴가 정상적으로 처리되었습니다."));
    }

    /**
     * 회원 정보 조회
     */
    @Override
    public ResponseEntity<?> getMemberInfo(String email) {

        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 회원의 이름, 계좌번호, 프로필 사진 반환
        MemberInfoRes memberInfoRes = new MemberInfoRes(
                member.getName(),
                member.getProfileImgLink(),
                member.getAccountNumber()
        );

        return ResponseEntity.ok(ApiResponse.onSuccess(memberInfoRes));

    }

    /**
     * 회원 이름 수정
     */
    @Override
    public ResponseEntity<?> updateName(String email, String newName) {

        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        member.changeName(newName);
        memberRepository.save(member);
        return ResponseEntity.ok(ApiResponse.onSuccess(ProfileUpdateRes.ofName(newName)));
    }

    /**
     * 회원 계좌 번호 수정
     */
    @Override
    public ResponseEntity<?> updateAccountNumber(String email, String newAccountNumber) {

        Member member = memberRepository.findMemberByEmail(email)
         .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        member.changeAccountNumber(newAccountNumber);
        memberRepository.save(member);
        return ResponseEntity.ok(ApiResponse.onSuccess(ProfileUpdateRes.ofAccountNumber(newAccountNumber)));
    }

    /**
     * 회원 프로필 이미지 수정
     */
    @Transactional
    @Override
    public ResponseEntity<?> updateProfileImgLink(String email, MultipartFile newProfileImg) throws IOException {

        // 1. 이메일로 회원 조회
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. 기존 프로필 이미지 삭제
        if (member.getProfileImgLink() != null && !member.getProfileImgLink().isEmpty()) {
            s3Service.deleteFileByURL(member.getProfileImgLink());  // URL에서 파일명을 추출하여 삭제
        }

        // 3. 새 프로필 이미지 업로드
        String newProfileImgLink = s3Service.uploadFile(newProfileImg);  // S3에 새 이미지 업로드

        // 4. 새 프로필 이미지 URL로 변경
        member.changeProfileImgLink(newProfileImgLink);

        // 5. DB에 저장
        memberRepository.save(member);

        return ResponseEntity.ok(ApiResponse.onSuccess(ProfileUpdateRes.ofProfileImgLink(newProfileImgLink)));
    }

    /**
     * 회원 프로필 이미지 삭제
     */
    @Transactional
    @Override
    public ResponseEntity<?> deleteProfileImgLink(String email) {

        // 1. 이메일로 회원 조회
        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        // 2. 기존 프로필 이미지 삭제
        String profileImgLink = member.getProfileImgLink();
        if (profileImgLink != null && !profileImgLink.isEmpty()) {
            s3Service.deleteFileByURL(profileImgLink);  // S3에서 파일 삭제
            member.changeProfileImgLink(null);  // 프로필 이미지 링크 null로 변경
            memberRepository.save(member);
        }

        return ResponseEntity.ok(ApiResponse.onSuccess("해당 회원의 프로필 이미지가 삭제되었습니다"));
    }
}


