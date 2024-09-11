package com.nsn.nsnupdatesbackend.notificationbatch;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.notification.Notification;
import com.nsn.nsnupdatesbackend.notification.NotificationRepository;
import com.nsn.nsnupdatesbackend.notificationwebsocket.NotificationWebSocketService;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationBatchService {

    private final NotificationRepository notificationRepository;
    private final NotificationBatchRepository notificationBatchRepository;
    private final NotificationBatchMapper notificationBatchMapper;
    private final NotificationWebSocketService notificationWebSocketService;
    private final AppUserService appUserService;

    @Autowired
    public NotificationBatchService(NotificationRepository notificationRepository,
                                    NotificationBatchRepository notificationBatchRepository,
                                    NotificationBatchMapper notificationBatchMapper,
                                    AppUserService appUserService,
                                    NotificationWebSocketService notificationWebSocketService
                                    ) {
        this.notificationRepository = notificationRepository;
        this.notificationBatchRepository = notificationBatchRepository;
        this.appUserService = appUserService;
        this.notificationBatchMapper = notificationBatchMapper;
        this.notificationWebSocketService = notificationWebSocketService;
    }

    public void sendBatchNotifications() {
        List<Notification> unsentNotifications = notificationRepository.findNotificationsByIsSentToUserIsFalse();

        if (unsentNotifications.isEmpty()) {
            return;
        }

        Map<AppUser, List<Notification>> map = unsentNotifications.stream().collect(Collectors.groupingBy(
            Notification::getAppUser)
        );

        if (map.isEmpty()) {
            return;
        }

        map.forEach((user, notifications) -> {
            Map<ENotificationType, List<Notification>> notificationTypeMap = notifications
                    .stream()
                    .collect(Collectors.groupingBy(Notification::getNotificationType));

            notificationTypeMap.forEach((notificationType, notificationList) -> {

                Map<Update, List<Notification>> notificationListForUpdate = notificationList.stream().collect(Collectors.groupingBy(Notification::getUpdate));

                notificationListForUpdate.forEach((update, notificationListForIndividualUpdate) -> {

                    String message = "";
                    if (notificationType == ENotificationType.NOTIFICATION_FOLLOWED_POSTED) {
                        message = "%s users you follow have posted".formatted(notificationListForIndividualUpdate.size());
                    } else if (notificationType == ENotificationType.NOTIFICATION_UPDATE_LIKED) {
                        message = "%s users have liked your update".formatted(notificationListForIndividualUpdate.size());
                    }

                    NotificationBatch notificationBatch = new NotificationBatch();
                    notificationBatch.setNotificationType(notificationType);
                    notificationBatch.setUpdate(update);
                    notificationBatch.setMessage(message);
                    notificationBatch.setAppUser(user);
                    notificationBatch.setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC")));

                    notificationListForIndividualUpdate.forEach(x -> {
                        x.setNotificationBatch(notificationBatch);
                        x.setIsSentToUser(true);
                    });

                    notificationBatch.setNotifications(notificationListForIndividualUpdate);
                    notificationBatchRepository.save(notificationBatch);

                    notificationWebSocketService.sendNotificationBatchToUser(
                            notificationBatch.getAppUser().getUsername(),
                            notificationBatchMapper.toNotificationBatchDto(notificationBatch)
                    );
                });

            });

        });
    }

    public List<NotificationBatchDto> getNotificationBatchesForUserWithUsername(String username) {
        AppUser appUser = appUserService.getUserByUsername(username);
        return notificationBatchRepository.findNotificationBatchByAppUser(appUser).stream().map(notificationBatchMapper::toNotificationBatchDto).toList();
    }
}
