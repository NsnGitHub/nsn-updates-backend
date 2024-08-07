package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.user.AppUserDto;

import java.time.ZonedDateTime;

public record UpdateDto(
        String content,
        ZonedDateTime createdAt,
        AppUserDto appUser
) {
}
