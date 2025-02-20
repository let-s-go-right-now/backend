package com.lets.go.right.now.domain.member.entity;

import com.lets.go.right.now.domain.member.dto.JoinReq;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Member")
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;
    private String name; // 회원 이름
    private String email;
    private String password;
    private String role; // 사용자 권한
    @Column(name = "account_number")
    private String accountNumber; // 계좌 번호
    @Column(name = "profile_image_link")
    private String profileImgLink; // 프로필 이미지 링크 - S3

    // == 편의 메소드 ==
    public static Member toEntity(JoinReq joinReq, BCryptPasswordEncoder passwordEncoder) {
        return Member.builder()
                .name(joinReq.name())
                .email(joinReq.email())
                .password(passwordEncoder.encode(joinReq.password())) // 비밀번호 암호화
                .role("ROLE_USER")
                .accountNumber(joinReq.accountNumber())
                .build();
    }

    public void changeProfileImgLink(String profileImgLink) {
        this.profileImgLink = profileImgLink;
    }
}
