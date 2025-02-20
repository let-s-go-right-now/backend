package com.lets.go.right.now.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private String email;
    private String password;
    private String role; // 사용자 권한
    @Column(name = "account_number")
    private String accountNumber; // 계좌 번호
    @Column(name = "profile_image_link")
    private String profileImgLink; // 프로필 이미지 링크 - S3
}
