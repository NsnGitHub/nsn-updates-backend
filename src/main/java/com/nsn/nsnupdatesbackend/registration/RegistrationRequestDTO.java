package com.nsn.nsnupdatesbackend.registration;

public record RegistrationRequestDTO(
        String username,
        String displayName,
        String email,
        String password
) {

}
