package com.nsn.nsnupdatesbackend.notificationbatch;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.notification.NotificationDto;

import java.time.ZonedDateTime;
import java.util.List;

public record NotificationBatchDto(
        Integer id,
        String message,
        List<NotificationDto> notificationDtoList,
        ENotificationType notificationType,
        ZonedDateTime createdAt,
        boolean isRead
) {
}
