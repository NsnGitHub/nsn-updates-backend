package com.nsn.nsnupdatesbackend.notification;

import com.nsn.nsnupdatesbackend.AbstractBaseTestContainer;
import com.nsn.nsnupdatesbackend.config.PaginationConfig;
import com.nsn.nsnupdatesbackend.enums.ENotificationType;
import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.followrequest.FollowRequestService;
import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatchDto;
import com.nsn.nsnupdatesbackend.notificationbatch.NotificationBatchService;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.update.UpdateService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

@Transactional
public class NotificationServiceTest extends AbstractBaseTestContainer {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationBatchService notificationBatchService;

    @Autowired
    private UpdateService updateService;

    @Autowired
    private FollowRequestService followRequestService;

    @Autowired
    private PaginationConfig paginationConfig;

    private static final AppUser user1 = new AppUser("nsntest1", "nsntest1", "nsntest1@test.com",
            "password");
    private static final AppUser user2 = new AppUser("nsntest2", "nsntest2", "nsntest2@test.com",
            "password");
    private static final AppUser user3 = new AppUser("nsntest3", "nsntest3", "nsntest3@test.com",
            "password");

    @BeforeAll
    public static void setUp(@Autowired AppUserService appUserService) {
        appUserService.saveUser(user1);
        appUserService.saveUser(user2);

        user3.setPrivacySetting(EPrivacySetting.PUBLIC);
        appUserService.saveUser(user3);
    }

    @AfterAll
    public static void cleanUp(@Autowired AppUserService appUserService) {
        appUserService.deleteUser(user1);
        appUserService.deleteUser(user2);
        appUserService.deleteUser(user3);
    }

    @Test
    void canCreateNotification() {
        Assertions.assertEquals(0, notificationService.getNotifications().size());

        notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOW_PUBLIC, Optional.empty());
        notificationService.createNotificationFromUserAndTarget(user2, user1, ENotificationType.NOTIFICATION_FOLLOW_PUBLIC, Optional.empty());

        Assertions.assertEquals(2, notificationService.getNotifications().size());
    }

    @Test
    void canGetNotificationForSpecificUser() {
        Assertions.assertEquals(0, notificationService.getNotifications().size());

        Notification notification = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOW_REQUEST, Optional.empty());
        notificationService.sendNotification(notification);

        Assertions.assertEquals(0, notificationService.getNotificationsForUserWithUsername(user1.getUsername()).size());
        Assertions.assertEquals(1, notificationService.getNotificationsForUserWithUsername(user2.getUsername()).size());
        Assertions.assertEquals(1, notificationService.getUnreadNotificationCountDto(user2.getUsername()).unreadFollowCount());

        Notification notification2 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.empty());
        notificationService.sendNotification(notification2);

        Assertions.assertEquals(0, notificationService.getNotificationsForUserWithUsername(user1.getUsername()).size());
        Assertions.assertEquals(2, notificationService.getNotificationsForUserWithUsername(user2.getUsername()).size());
        Assertions.assertEquals(1, notificationService.getUnreadNotificationCountDto(user2.getUsername()).unreadUpdateCount());
    }

    @Test
    void canMarkNotificationsAsRead() {
        Notification notification = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.empty());
        notificationService.sendNotification(notification);

        Assertions.assertEquals(1, notificationService.getUnreadNotificationCountDto(user2.getUsername()).unreadUpdateCount());

        notificationService.readNotification(user2.getUsername(), notification.getId());

        Assertions.assertEquals(0, notificationService.getUnreadNotificationCountDto(user2.getUsername()).unreadUpdateCount());
    }

    @Test
    void cannotMarkNotificationsAsReadIfNotSpecifiedUser() {
        Notification notification = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.empty());
        notificationService.sendNotification(notification);

        Assertions.assertEquals(1, notificationService.getUnreadNotificationCountDto(user2.getUsername()).unreadUpdateCount());

        Assertions.assertThrows(AccessDeniedException.class, () -> notificationService.readNotification(user1.getUsername(), notification.getId()));
    }

    @Test
    void canCreateNotificationsAndBatchThemUp() {
        Update update = new Update("TEST POST", user1);
        updateService.saveUpdate(update);

        Assertions.assertEquals(0, notificationService.getNotifications().size());

        notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.of(update));
        notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.of(update));
        notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.of(update));

        // This would be called through a scheduleable
        notificationBatchService.sendBatchNotifications();

        // Normal notification stuff
        Assertions.assertEquals(3, notificationService.getNotificationsForUserWithUsername(user2.getUsername()).size());
        Assertions.assertEquals(3, notificationService.getNotifications().size());

        // Batch notification stuff
        List<NotificationBatchDto> notificationBatchList = notificationBatchService.getNotificationBatchesForUserWithUsername(user2.getUsername());

        Assertions.assertEquals(1, notificationBatchList.size());

        NotificationBatchDto batch = notificationBatchList.getFirst();

        Assertions.assertEquals(3, batch.notificationDtoList().size());
    }

    @Test
    void canCreateNotificationsForFollowEvents() throws BadRequestException {
        followRequestService.createFollowRequest(user1.getUsername(), user2.getUsername(), true);
        followRequestService.createFollowRequest(user1.getUsername(), user3.getUsername(), true);

        Assertions.assertEquals(0, notificationService.getFollowNotificationsForUserWithUsername(user1.getUsername()).size());
        Assertions.assertEquals(1, notificationService.getFollowNotificationsForUserWithUsername(user2.getUsername()).size());
        Assertions.assertEquals(1, notificationService.getFollowNotificationsForUserWithUsername(user3.getUsername()).size());


        followRequestService.acceptFollowRequest(user1.getUsername(), user2.getUsername(), true);

        Assertions.assertEquals(1, notificationService.getFollowNotificationsForUserWithUsername(user1.getUsername()).size());
        Assertions.assertEquals(1, notificationService.getFollowNotificationsForUserWithUsername(user2.getUsername()).size());

    }

    @Test
    public void canViewPaginatedNotifications1() {
        Notification notification1 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.empty());
        Notification notification2 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.empty());
        Notification notification3 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.empty());
        Notification notification4 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.empty());
        Notification notification5 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.empty());
        Notification notification6 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOWED_POSTED, Optional.empty());

        notificationService.sendNotification(notification1);
        notificationService.sendNotification(notification2);
        notificationService.sendNotification(notification3);
        notificationService.sendNotification(notification4);
        notificationService.sendNotification(notification5);
        notificationService.sendNotification(notification6);

        final int PAGE0 = 0;
        final int PAGE1 = 1;

        Assertions.assertEquals(5, notificationService.getUpdateNotificationsForUsersWithUsernamePaginated(user2.getUsername(), PAGE0).size());
        Assertions.assertEquals(1, notificationService.getUpdateNotificationsForUsersWithUsernamePaginated(user2.getUsername(), PAGE1).size());
    }

    @Test
    public void canViewPaginatedNotifications2() {
        Notification notification1 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOW_REQUEST, Optional.empty());
        Notification notification2 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOW_REQUEST, Optional.empty());
        Notification notification3 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOW_REQUEST, Optional.empty());
        Notification notification4 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOW_REQUEST, Optional.empty());
        Notification notification5 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOW_REQUEST, Optional.empty());
        Notification notification6 = notificationService.createNotificationFromUserAndTarget(user1, user2, ENotificationType.NOTIFICATION_FOLLOW_REQUEST, Optional.empty());

        notificationService.sendNotification(notification1);
        notificationService.sendNotification(notification2);
        notificationService.sendNotification(notification3);
        notificationService.sendNotification(notification4);
        notificationService.sendNotification(notification5);
        notificationService.sendNotification(notification6);

        final int PAGE0 = 0;
        final int PAGE1 = 1;

        Assertions.assertEquals(5, notificationService.getFollowNotificationsForUserWithUsernamePaginated(user2.getUsername(), PAGE0).size());
        Assertions.assertEquals(1, notificationService.getFollowNotificationsForUserWithUsernamePaginated(user2.getUsername(), PAGE1).size());
    }
}
