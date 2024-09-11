package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findNotificationsByIsSentToUserIsFalse();
    List<Notification> findNotificationsByIsSentToUserIsTrueAndAppUser(AppUser user);
    List<Notification> findNotificationsByIsSentToUserIsTrueAndAppUserAndNotificationTypeIsIn(AppUser user, List<ENotificationType> notificationTypeList);
}
