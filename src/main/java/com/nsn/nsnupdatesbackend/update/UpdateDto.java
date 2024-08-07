package com.nsn.nsnupdatesbackend.update;

import java.time.ZonedDateTime;

public record UpdateDto(
        Integer id,
        String content,
        ZonedDateTime createdAt,
        int numberOfLikes,
        UpdatePosterDto appUser
) {
}
