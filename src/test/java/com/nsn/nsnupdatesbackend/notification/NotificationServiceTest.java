package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.AbstractBaseTestContainer;
import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
public class NotificationServiceTest extends AbstractBaseTestContainer {

    @Autowired
    private NotificationService notificationService;

    private static final AppUser user1 = new AppUser("nsntest1", "nsntest1", "nsntest1@test.com",
            "password");
    private static final AppUser user2 = new AppUser("nsntest2", "nsntest2", "nsntest2@test.com",
            "password");

    @BeforeAll
    public static void setUp(@Autowired AppUserService appUserService) {
        appUserService.saveUser(user1);
        appUserService.saveUser(user2);
    }

    @AfterAll
    public static void cleanUp(@Autowired AppUserService appUserService) {
        appUserService.deleteUser(user1);
        appUserService.deleteUser(user2);
    }

    @Test
    void canCreateNotification() {
        assertEquals(0, notificationService.getNotifications().size());

        notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_UPDATE_LIKED);
        notificationService.createNotificationFromUserAndTarget(user2, user1, ENotificationType.NOTIFICATION_UPDATE_LIKED);

        assertEquals(2, notificationService.getNotifications().size());
    }

    @Test
    void canGetNotificationForSpecificUser() {
        assertEquals(0, notificationService.getNotifications().size());

        Notification notification = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_UPDATE_LIKED);
        notificationService.sendNotification(notification);

        assertEquals(0, notificationService.getNotificationsForUserWithUsername(user1.getUsername()).size());
        assertEquals(1, notificationService.getNotificationsForUserWithUsername(user2.getUsername()).size());
    }

}
