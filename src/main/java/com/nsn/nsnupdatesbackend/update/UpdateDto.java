package com.nsn.nsnupdatesbackend.update;

import java.time.ZonedDateTime;

public record UpdateDto(
        String content,
        ZonedDateTime createdAt,
        UpdatePosterDto appUser
) {
}
