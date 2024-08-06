package com.nsn.nsnupdatesbackend.auth;

public record AuthDto(
        String accessToken,
        String refreshToken
) {
}
