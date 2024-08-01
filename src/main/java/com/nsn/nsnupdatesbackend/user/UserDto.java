package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.enums.PrivacySetting;

public record UserDto(
        String username,
        String displayName,
        String email,
        String bio,
        PrivacySetting privacySetting
) {

}