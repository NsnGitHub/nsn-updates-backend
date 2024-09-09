package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findNotificationsByIsSentToUserIsFalse();
    List<Notification> findNotificationsByIsSentToUserIsTrueAndAppUser(AppUser user);
}
