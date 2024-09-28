package com.nsn.nsnupdatesbackend.notificationbatch;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.notification.Notification;
import com.nsn.nsnupdatesbackend.notification.NotificationRepository;
import com.nsn.nsnupdatesbackend.notificationwebsocket.NotificationWebSocketService;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.transaction.Transactional;
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

    @Transactional
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

        List<NotificationBatch> batches = new ArrayList<>();

        map.forEach((user, notifications) -> {
            Map<ENotificationType, List<Notification>> notificationTypeMap = notifications
                    .stream()
                    .collect(Collectors.groupingBy(Notification::getNotificationType));

            notificationTypeMap.forEach((notificationType, notificationList) -> {

                if (notificationType == ENotificationType.NOTIFICATION_UPDATE_LIKED) {
                    Map<Update, List<Notification>> notificationListForUpdate = notificationList.stream().collect(Collectors.groupingBy(Notification::getUpdate));

                    notificationListForUpdate.forEach((update, notificationListForIndividualUpdate) -> {

                        String message = getString(notificationType, notificationListForIndividualUpdate);

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
                        batches.add(notificationBatch);
                    });
                } else {
                    // Can only be NOTIFICATION_FOLLOWED_POSTED

                    String message = getString(notificationType, notificationList);

                    NotificationBatch notificationBatch = new NotificationBatch();
                    notificationBatch.setNotificationType(notificationType);
                    notificationBatch.setMessage(message);
                    notificationBatch.setAppUser(user);
                    notificationBatch.setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC")));

                    notificationList.forEach(x -> {
                        x.setNotificationBatch(notificationBatch);
                        x.setIsSentToUser(true);
                    });

                    notificationBatch.setNotifications(notificationList);
                    batches.add(notificationBatch);
                }
            });
        });

        notificationBatchRepository.saveAll(batches);
        batches.forEach(batch ->
            notificationWebSocketService.sendNotificationBatchToUser(
                batch.getAppUser().getUsername(),
                notificationBatchMapper.toNotificationBatchDto(batch)
            )
        );
    }

    private static String getString(ENotificationType notificationType, List<Notification> notificationListForIndividualUpdate) {
        String postMessageTemplate = "%s new posts from users you follow";
        String likeMessageTemplate = "%s users have liked your update";

        if (notificationListForIndividualUpdate.size() == 1) {
            postMessageTemplate = "1 user you follow have posted";
            likeMessageTemplate = "1 user have liked your update";
        }

        return switch (notificationType) {
            case NOTIFICATION_FOLLOWED_POSTED ->
                    postMessageTemplate.formatted(notificationListForIndividualUpdate.size());
            case NOTIFICATION_UPDATE_LIKED ->
                    likeMessageTemplate.formatted(notificationListForIndividualUpdate.size());
            default -> "You have a new notification"; // A fallback message
        };
    }

    public List<NotificationBatchDto> getNotificationBatchesForUserWithUsername(String username) {
        AppUser appUser = appUserService.getUserByUsername(username);
        return notificationBatchRepository.findNotificationBatchByAppUser(appUser).stream().map(notificationBatchMapper::toNotificationBatchDto).toList();
    }
}
