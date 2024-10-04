package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;

import java.time.ZonedDateTime;

public record AppUserDto(
        String username,
        String displayName,
        String bio,
        EPrivacySetting privacySetting,
        ZonedDateTime createdAt,
        Integer numberOfFollowers,
        Integer numberFollowing
) {

}