package com.lets.go.right.now.domain.invite.dto;

public record InviteRes(
        boolean isSuccess,
        String code,
        String message,
        String result
) {
    public static InviteRes success(String code, String message, String result) {
        return new InviteRes(true, code, message, result);
    }

    public static InviteRes error(String code, String message) {
        return new InviteRes(false, code, message, null);
    }
}
