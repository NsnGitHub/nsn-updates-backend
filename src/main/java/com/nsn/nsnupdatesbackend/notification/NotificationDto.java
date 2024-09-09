package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.update.UpdateDto;
import com.nsn.nsnupdatesbackend.user.AppUserDto;

import java.time.ZonedDateTime;

public record NotificationDto(
        AppUserDto actorUser,
        UpdateDto update,
        ENotificationType eNotificationType,
        boolean isRead,
        ZonedDateTime createdAt
) {
}
