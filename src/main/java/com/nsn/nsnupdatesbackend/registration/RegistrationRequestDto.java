package com.nsn.nsnupdatesbackend.registration;

public record RegistrationRequestDto(
        String username,
        String displayName,
        String email,
        String password
) {

}
