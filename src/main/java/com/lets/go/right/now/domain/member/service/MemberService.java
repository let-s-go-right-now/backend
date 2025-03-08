package com.lets.go.right.now.domain.member.service;

import com.lets.go.right.now.domain.member.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MemberService {
    ResponseEntity<?> login(LoinReq dto);
    ResponseEntity<?> join(JoinReq joinReq, MultipartFile image) throws IOException;

    ResponseEntity<?> getAccountNumber(AccountReq accountReq);
    void deleteMember(String email);

    MemberInfoRes getMemberInfo(String email);

    ProfileUpdateRes updateName(String email, String newName);

    ResponseEntity<?> updateAccountNumber(String email, String newAccountNumber);

    ResponseEntity<?> updateProfileImgLink(String email, MultipartFile newProfileImg) throws IOException;
}
