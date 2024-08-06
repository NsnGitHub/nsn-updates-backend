package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;

public record AppUserDto(
        String username,
        String displayName,
        String email,
        String bio,
        EPrivacySetting privacySetting
) {

}