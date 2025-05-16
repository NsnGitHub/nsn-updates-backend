package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import jakarta.annotation.Nullable;

import java.time.ZonedDateTime;

public record AppUserDto(
        @Nullable
        String username,
        @Nullable
        String displayName,
        @Nullable
        String bio,
        @Nullable
        EPrivacySetting privacySetting,
        @Nullable
        ZonedDateTime createdAt,
        @Nullable
        Integer numberOfFollowers,
        @Nullable
        Integer numberFollowing
) {

}