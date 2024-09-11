package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatchService;
import com.nsn.nsnupdatesbackend.notificationwebsocket.NotificationWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationBatchService notificationBatchService;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationBatchService notificationBatchService, NotificationService notificationService) {
        this.notificationBatchService = notificationBatchService;
        this.notificationService = notificationService;
    }

    @GetMapping()
    public ResponseEntity<?> getNotifications(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.getNotificationsForUserWithUsername(principal.getName()));
    }

    @GetMapping("/batch")
    public ResponseEntity<?> getNotificationBatch(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(notificationBatchService.getNotificationBatchesForUserWithUsername(principal.getName()));
    }

    @GetMapping("/follow")
    public ResponseEntity<List<NotificationDto>> getFollowNotifications(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.getFollowNotificationsForUserWithUsername(principal.getName()));
    }

}
