package com.nsn.nsnupdatesbackend.notification;

import jakarta.validation.constraints.NotNull;

public record NotificationRequestDto(
        @NotNull
        Integer id
) {
}
