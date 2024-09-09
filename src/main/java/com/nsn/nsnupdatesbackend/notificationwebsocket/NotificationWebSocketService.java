package com.nsn.nsnupdatesbackend.notificationwebsocket;

import com.nsn.nsnupdatesbackend.notification.Notification;
import com.nsn.nsnupdatesbackend.notification.NotificationDto;
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

    public void sendStringToUser(String username, String message) {
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/reply", message);
    }

    public void sendNotificationToUser(String username, NotificationDto notificationDto) {
        simpMessagingTemplate.convertAndSendToUser(username, "/queue/notification", notificationDto);
    }
}
