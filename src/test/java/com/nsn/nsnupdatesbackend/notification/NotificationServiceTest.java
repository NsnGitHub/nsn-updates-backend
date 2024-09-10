package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.AbstractBaseTestContainer;
import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatch;
import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatchDto;
import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatchService;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.update.UpdateService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
public class NotificationServiceTest extends AbstractBaseTestContainer {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationBatchService notificationBatchService;

    @Autowired
    private UpdateService updateService;

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

    @Test
    void canCreateNotificationsAndBatchThemUp() {
        Update update = new Update("TEST POST", user1);
        updateService.saveUpdate(update);

        assertEquals(0, notificationService.getNotifications().size());

        Notification notification1 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_UPDATE_LIKED);
        Notification notification2 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_UPDATE_LIKED);
        Notification notification3 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_UPDATE_LIKED);

        notification1.setUpdate(update);
        notification2.setUpdate(update);
        notification3.setUpdate(update);

        notificationBatchService.sendBatchNotifications();

        // Normal notification stuff
        assertEquals(3, notificationService.getNotificationsForUserWithUsername(user2.getUsername()).size());
        assertEquals(3, notificationService.getNotifications().size());

        // Batch notification stuff
        List<NotificationBatchDto> notificationBatchList = notificationBatchService.getNotificationBatchesForUserWithUsername(user2.getUsername());

        assertEquals(1, notificationBatchList.size());

        NotificationBatchDto batch = notificationBatchList.getFirst();

        assertEquals(3, batch.notificationDtoList().size());
    }

}
