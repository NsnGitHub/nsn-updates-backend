package com.nsn.nsnupdatesbackend.update;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;

public record UpdatePostReqDto(
        @NotNull(message = "Update content cannot be null")
        @Size(min = 1, max = 1000, message = "Update content should be between 1 and 1000 characters")
        String content,
        @Nullable
        Integer id
) {
}
