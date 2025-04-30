package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.notificationwebsocket.NotificationWebSocketService;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final AppUserService appUserService;
    private final NotificationWebSocketService notificationWebSocketService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               NotificationMapper notificationMapper,
                               AppUserService appUserService,
                               NotificationWebSocketService notificationWebSocketService) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.appUserService = appUserService;
        this.notificationWebSocketService = notificationWebSocketService;
    }

    public List<NotificationDto> getNotifications() {
        return notificationRepository.findAll().stream().map(notificationMapper::toNotificationDto).collect(Collectors.toList());
    }

    public List<NotificationDto> getNotificationsForUserWithUsername(String username) {
        AppUser user = appUserService.getUserByUsername(username);
        return notificationRepository.findNotificationsByIsSentToUserIsTrueAndAppUser(user).stream().map(notificationMapper::toNotificationDto).collect(Collectors.toList());
    }

    public List<NotificationDto> getFollowNotificationsForUserWithUsername(String username) {
        AppUser user = appUserService.getUserByUsername(username);
        List<Notification> notificationlist = notificationRepository
                .findNotificationsByIsSentToUserIsTrueAndAppUserAndNotificationTypeIsIn(
                        user,
                        Arrays.asList(
                                ENotificationType.NOTIFICATION_FOLLOW_ACCEPTED,
                                ENotificationType.NOTIFICATION_FOLLOW_REQUEST,
                                ENotificationType.NOTIFICATION_FOLLOW_PUBLIC
                        )
                );

        return notificationlist.stream().map(notificationMapper::toNotificationDto).collect(Collectors.toList());
    }

    private int getUnreadFollowNotificationsCount(String username) {
        AppUser user = appUserService.getUserByUsername(username);
        List<ENotificationType> friendRequestTypes = new ArrayList<>();
        friendRequestTypes.add(ENotificationType.NOTIFICATION_FOLLOW_REQUEST);
        friendRequestTypes.add(ENotificationType.NOTIFICATION_FOLLOW_ACCEPTED);
        friendRequestTypes.add(ENotificationType.NOTIFICATION_FOLLOW_PUBLIC);

        return notificationRepository.countByAppUserAndNotificationTypeIsInAndIsReadAndIsSentToUser(user, friendRequestTypes, false, true);
    }

    private int getUnreadNotificationsCount(String username) {
        AppUser user = appUserService.getUserByUsername(username);
        List<ENotificationType> notificationTypes = new ArrayList<>();
        notificationTypes.add(ENotificationType.NOTIFICATION_UPDATE_LIKED);
        notificationTypes.add(ENotificationType.NOTIFICATION_FOLLOWED_POSTED);
        notificationTypes.add(ENotificationType.NOTIFICATION_UPDATE_COMMENTED);

        return notificationRepository.countByAppUserAndNotificationTypeIsInAndIsReadAndIsSentToUser(user, notificationTypes, false, true);
    }

    public NotificationCountDto getUnreadNotificationCountDto(String username) {
        int friendNotificationsCount = getUnreadFollowNotificationsCount(username);
        int notificationsCount = getUnreadNotificationsCount(username);

        return new NotificationCountDto(friendNotificationsCount, notificationsCount);
    }

    public Notification createNotificationFromUserAndTarget(AppUser user, AppUser target,
                                                     ENotificationType  eNotificationType, Optional<Update> update) {
       Notification notification = new Notification(user, target, eNotificationType);
       notification.setUpdate(update.orElse(null));

       // Send out follow requests whenever made.
       if (eNotificationType == ENotificationType.NOTIFICATION_FOLLOW_ACCEPTED ||
               eNotificationType == ENotificationType.NOTIFICATION_FOLLOW_REQUEST ||
               eNotificationType == ENotificationType.NOTIFICATION_FOLLOW_PUBLIC) {
            sendNotification(notification);
                notificationWebSocketService.sendNotificationForFollowsToUser(target.getUsername(),
                        notificationMapper.toNotificationDto(notification));
       } else {
           notificationRepository.save(notification);
       }

       return notification;
    }

    public void sendNotification(Notification notification) {
        notification.setIsSentToUser(true);
        notificationRepository.save(notification);
    }
}
