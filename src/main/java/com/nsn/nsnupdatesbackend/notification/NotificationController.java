package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatchService;
import com.nsn.nsnupdatesbackend.notificationwebsocket.NotificationWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationBatchService notificationBatchService;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationBatchService notificationBatchService, NotificationService notificationService, NotificationWebSocketService notificationWebSocketService) {
        this.notificationBatchService = notificationBatchService;
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.getNotifications());
    }

    @GetMapping("/notifications2")
    public ResponseEntity<?> getNotifications2(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.getNotificationsForUserWithUsername(principal.getName()));
    }

    @GetMapping("/notifications/batch")
    public void test() {
        notificationBatchService.sendBatchNotifications();
    }

    @GetMapping("/notifications/{id}")
    public void getNotificationDetailed(@PathVariable("id") String id, Principal principal) {

    }

    @GetMapping("/notifications/test")
    public void createTestNotification() {
        notificationBatchService.createNotifications();
    }

}
