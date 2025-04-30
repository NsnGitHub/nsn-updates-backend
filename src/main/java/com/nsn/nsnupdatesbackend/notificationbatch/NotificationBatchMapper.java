package com.nsn.nsnupdatesbackend.notificationbatch;

import com.nsn.nsnupdatesbackend.notification.NotificationDto;
import com.nsn.nsnupdatesbackend.notification.NotificationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationBatchMapper {

    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationBatchMapper(NotificationMapper notificationMapper) {
        this.notificationMapper = notificationMapper;
    }

    public NotificationBatchDto toNotificationBatchDto(NotificationBatch notificationBatch) {
        List<NotificationDto> notificationDtoList = notificationBatch.getNotifications()
                .stream()
                .map(notificationMapper::toNotificationDto)
                .toList();

        return new NotificationBatchDto(
                notificationBatch.getId(),
                notificationBatch.getMessage(),
                notificationBatch.getSizeOfBatch(),
                notificationDtoList,
                notificationBatch.getNotificationType(),
                notificationBatch.getCreatedAt(),
                notificationBatch.getIsRead()
        );
    }
}
