package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationBatchService {

    private final NotificationRepository notificationRepository;
    private final AppUserService appUserService;

    @Autowired
    public NotificationBatchService(NotificationRepository notificationRepository, AppUserService appUserService) {
        this.notificationRepository = notificationRepository;
        this.appUserService = appUserService;
    }

    public void sendBatchNotifications() {
        List<Notification> unsentNotifications = notificationRepository.findNotificationsByIsSentToUserIsFalse();

        Map<AppUser, List<Notification>> map = unsentNotifications.stream().collect(Collectors.groupingBy(
            Notification::getAppUser)
        );

        map.forEach((user, notifications) -> {
            Map<ENotificationType, List<Notification>> notificationTypeMap = notifications
                    .stream()
                    .collect(Collectors.groupingBy(Notification::getNotificationType));

            notificationTypeMap.forEach((notificationType, notificationList) -> {
                System.out.println(notificationType.toString() + " " + notificationList.size() + " notifications.");
            });

        });

    }

    public void createNotifications(String username) {
        AppUser user = appUserService.getUserByUsername(username);

        Notification notification1 = new Notification(user, user, ENotificationType.NOTIFICATION_UPDATE_LIKED);
        Notification notification2 = new Notification(user, user, ENotificationType.NOTIFICATION_UPDATE_LIKED);
        Notification notification3 = new Notification(user, user, ENotificationType.NOTIFICATION_FOLLOWED_POSTED);
        Notification notification4 = new Notification(user, user, ENotificationType.NOTIFICATION_FOLLOWED_POSTED);
        Notification notification5 = new Notification(user, user, ENotificationType.NOTIFICATION_FOLLOWED_POSTED);
        Notification notification6 = new Notification(user, user, ENotificationType.NOTIFICATION_FOLLOWED_POSTED);

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);
        notificationRepository.save(notification4);
        notificationRepository.save(notification5);
        notificationRepository.save(notification6);
    }
}
