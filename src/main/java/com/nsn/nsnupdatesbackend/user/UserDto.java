package com.nsn.nsnupdatesbackend.user;

public record UserDto(
        String username,
        String displayName,
        String email,
        String password
) {

}