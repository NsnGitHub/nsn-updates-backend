package com.nsn.nsnupdatesbackend.followrequest;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FollowRequestDto(
        @Nullable
        @Pattern(
                regexp = "^[a-zA-Z0-9]{3,15}$",
                message = "Username must be between 3 and 15 characters and can only contain letters and digits"
        )
        String requesterUsername,

        @Nullable
        @Pattern(
                regexp = "^[a-zA-Z0-9]{3,15}$",
                message = "Username must be between 3 and 15 characters and can only contain letters and digits"
        )
        String targetUsername
) {
}
