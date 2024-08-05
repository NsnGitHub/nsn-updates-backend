package com.nsn.nsnupdatesbackend.auth;

public record AuthLogInReq(
        String username,
        String password
) {

}
