package com.nsn.nsnupdatesbackend.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationBatchService notificationBatchService;

    @Autowired
    public NotificationController(NotificationBatchService notificationBatchService) {
        this.notificationBatchService = notificationBatchService;
    }

    @GetMapping("/test")
    public void test() {
        notificationBatchService.sendBatchNotifications();
    }

    @GetMapping("/createtest")
    public void create_test(Principal principal) {
        notificationBatchService.createNotifications(principal.getName());
    }
}
