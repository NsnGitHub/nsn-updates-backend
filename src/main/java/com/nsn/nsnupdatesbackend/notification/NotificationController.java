package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
        notificationBatchService.sendBatchNotifications();
        return ResponseEntity.status(HttpStatus.OK).body(notificationBatchService.getNotificationBatchesForUserWithUsername(principal.getName()));
    }

    @GetMapping("/follow")
    public ResponseEntity<List<NotificationDto>> getFollowNotifications(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(notificationService.getFollowNotificationsForUserWithUsername(principal.getName()));
    }

    @GetMapping("/follow/query")
    public ResponseEntity<List<NotificationDto>> getFollowNotificationsPaginated(Principal principal, @RequestParam(required = false) Integer page) {
        return ResponseEntity.ok().body(notificationService.getFollowNotificationsForUserWithUsernamePaginated(principal.getName(), page));
    }

    @GetMapping("/update/query")
    public ResponseEntity<List<NotificationDto>> getUpdateNotifications(Principal principal, @RequestParam(required = false) Integer page) {
        return ResponseEntity.ok().body(notificationService.getUpdateNotificationsForUsersWithUsernamePaginated(principal.getName(), page));
    }

    @GetMapping("/unread/count")
    public ResponseEntity<NotificationCountDto> getUnreadFollowNotifications(Principal principal) {
        return ResponseEntity.ok().body(notificationService.getUnreadNotificationCountDto(principal.getName()));
    }

    @PostMapping("/read")
    public ResponseEntity<?> readNotification(Principal principal, @Valid @RequestBody NotificationRequestDto notificationRequestDto) {
        notificationService.readNotification(principal.getName(), notificationRequestDto.id());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ROLE_GUEST')")
    @GetMapping("/guest")
    public ResponseEntity<List<NotificationDto>> getGuestNotifications() {
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
