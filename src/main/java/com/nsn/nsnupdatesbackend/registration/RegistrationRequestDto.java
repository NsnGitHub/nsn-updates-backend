package com.nsn.nsnupdatesbackend.registration;

import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegistrationRequestDto(
        @NotBlank(message = "Username cannot be blank")
        @Pattern(
                regexp = "^[a-zA-Z0-9]{3,15}$",
                message = "Username must be between 3 and 15 characters and can only contain letters and digits"
        )
        String username,

        @NotBlank(message = "Display name cannot be blank")
        @Pattern(
                regexp = "^[a-zA-Z ]{3,15}$",
                message = "Display name must be between 3 and 15 characters and can only contain letters and spaces"
        )
        String displayName,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email is not valid")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Pattern(
                regexp = "^[\\x21-\\x7E]{5,}$",
                message = "Password must be at least 5 characters long and can contain any printable ASCII character" +
                        "except space"
        )
        String password,

        @NotNull(message = "Privacy setting must be set")
        EPrivacySetting ePrivacySetting
) {

}
