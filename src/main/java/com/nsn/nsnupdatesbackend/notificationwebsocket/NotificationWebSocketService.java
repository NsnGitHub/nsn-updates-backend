package com.nsn.nsnupdatesbackend.notificationwebsocket;

import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import com.nsn.nsnupdatesbackend.notification.NotificationDto;
import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatchDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

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
