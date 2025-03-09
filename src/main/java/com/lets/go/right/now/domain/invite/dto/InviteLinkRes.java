package com.lets.go.right.now.domain.invite.dto;

public record InviteLinkRes(String invitelink) {

    public static InviteLinkRes of(String invitelink) {
        return new InviteLinkRes(invitelink);
    }
}

