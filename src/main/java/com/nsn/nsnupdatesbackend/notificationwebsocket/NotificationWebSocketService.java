package com.nsn.nsnupdatesbackend.notificationwebsocket;

import com.nsn.nsnupdatesbackend.notification.NotificationDto;
import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatchDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationWebSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public NotificationWebSocketService(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendNotificationBatchToUser(String username, NotificationBatchDto notificationBatchDto) {
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/notification/batch", notificationBatchDto);
    }

    public void sendNotificationForFollowsToUser(String username, NotificationDto notificationDto) {
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/notification/follow", notificationDto);
    }
}
