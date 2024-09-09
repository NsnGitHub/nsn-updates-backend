package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final AppUserService appUserService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, NotificationMapper notificationMapper, AppUserService appUserService) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.appUserService = appUserService;
    }

    public List<NotificationDto> getNotifications() {
        return notificationRepository.findAll().stream().map(notificationMapper::toNotificationDto).collect(Collectors.toList());
    }

    public List<NotificationDto> getNotificationsForUser() {
        AppUser user = appUserService.getUserByUsername("test");
        return notificationRepository.findNotificationsByIsSentToUserIsTrueAndAppUser(user).stream().map(notificationMapper::toNotificationDto).collect(Collectors.toList());
    }

    public void createNotificationFromUserAndTarget(AppUser user, AppUser target,
                                                     ENotificationType  eNotificationType) {
       Notification notification = new Notification(target, user, eNotificationType);
       notificationRepository.save(notification);
    }
}
