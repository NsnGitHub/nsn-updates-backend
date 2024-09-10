package com.nsn.nsnupdatesbackend.notificationbatch;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.notification.Notification;
import com.nsn.nsnupdatesbackend.notification.NotificationRepository;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.update.UpdateService;
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
    private final AppUserService appUserService;

    private final UpdateService updateService;
    private final NotificationBatchMapper notificationBatchMapper;

    @Autowired
    public NotificationBatchService(NotificationRepository notificationRepository,
                                    NotificationBatchRepository notificationBatchRepository,
                                    AppUserService appUserService,
                                    UpdateService updateService,
                                    NotificationBatchMapper notificationBatchMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationBatchRepository = notificationBatchRepository;
        this.appUserService = appUserService;
        this.updateService = updateService;
        this.notificationBatchMapper = notificationBatchMapper;
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

                });

            });

        });
    }

    public List<NotificationBatchDto> getNotificationBatchesForUserWithUsername(String username) {
        AppUser appUser = appUserService.getUserByUsername(username);
        return notificationBatchRepository.findNotificationBatchByAppUser(appUser).stream().map(notificationBatchMapper::toNotificationBatchDto).toList();
    }

    public void createNotificationBatch() {
        AppUser user = new AppUser("test", "test", "test1@test.com", "test");
        appUserService.saveUser(user);

        AppUser receiver = appUserService.getUserByUsername("testreceiver");

        Update update1 = new Update("Test Post1", user);
        Update update2 = new Update("Test Post2", user);

        updateService.saveUpdate(update1);
        updateService.saveUpdate(update2);

        Notification notification1 = new Notification(user, receiver, ENotificationType.NOTIFICATION_UPDATE_LIKED);
        Notification notification2 = new Notification(user, receiver, ENotificationType.NOTIFICATION_UPDATE_LIKED);
        Notification notification3 = new Notification(user, receiver, ENotificationType.NOTIFICATION_FOLLOWED_POSTED);
        Notification notification4 = new Notification(user, receiver, ENotificationType.NOTIFICATION_FOLLOWED_POSTED);
        Notification notification5 = new Notification(user, receiver, ENotificationType.NOTIFICATION_FOLLOWED_POSTED);
        Notification notification6 = new Notification(user, receiver, ENotificationType.NOTIFICATION_FOLLOWED_POSTED);

        notification1.setUpdate(update1);
        notification2.setUpdate(update1);
        notification3.setUpdate(update1);
        notification4.setUpdate(update1);
        notification5.setUpdate(update2);
        notification6.setUpdate(update2);

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);
        notificationRepository.save(notification4);
        notificationRepository.save(notification5);
        notificationRepository.save(notification6);

//        notificationWebSocketService.sendNotificationToUser(receiver.getUsername(), notificationMapper.toNotificationDto(notification1));
//        notificationWebSocketService.sendNotificationToUser(receiver.getUsername(), notificationMapper.toNotificationDto(notification2));
//        notificationWebSocketService.sendNotificationToUser(receiver.getUsername(), notificationMapper.toNotificationDto(notification3));
//        notificationWebSocketService.sendNotificationToUser(receiver.getUsername(), notificationMapper.toNotificationDto(notification4));
//        notificationWebSocketService.sendNotificationToUser(receiver.getUsername(), notificationMapper.toNotificationDto(notification5));
//        notificationWebSocketService.sendNotificationToUser(receiver.getUsername(), notificationMapper.toNotificationDto(notification6));

    }
}
