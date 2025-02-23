package com.lets.go.right.now.domain.member.entity;

import com.lets.go.right.now.domain.expense.entity.ExcludedMember;
import com.lets.go.right.now.domain.expense.entity.SettlementResult;
import com.lets.go.right.now.domain.member.dto.JoinReq;
import com.lets.go.right.now.domain.trip.entity.ScrappedTrip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
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

    // == 편의 메소드 == //
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

    // == 연관 관계 설정 == //

    // 내가 제외된 지출
    @Builder.Default
    @OneToMany(mappedBy = "excludedMember", cascade = CascadeType.ALL)
    List<ExcludedMember> excludedMemberList = new ArrayList<>();

    // 내가 참여 중인 여행
    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    List<TripMember> tripList = new ArrayList<>();

    // 내가 스크랩한 여행
    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    List<ScrappedTrip> scrappedTripList = new ArrayList<>();

    // 내가 보내야 할 정산 결과
    @Builder.Default
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    List<SettlementResult> settlementSendResults = new ArrayList<>();

    // 내가 받아야 할 정산 결과
    @Builder.Default
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    List<SettlementResult> settlementReceiveResults = new ArrayList<>();
}
