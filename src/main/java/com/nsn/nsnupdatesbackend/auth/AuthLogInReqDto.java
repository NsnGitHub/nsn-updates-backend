package com.nsn.nsnupdatesbackend.auth;

public record AuthLogInReqDto(
        String username,
        String password
) {

}
