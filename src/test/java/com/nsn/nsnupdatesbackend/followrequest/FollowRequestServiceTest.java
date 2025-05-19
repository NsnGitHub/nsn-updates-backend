package com.nsn.nsnupdatesbackend.followrequest;

import com.nsn.nsnupdatesbackend.AbstractBaseTestContainer;
import com.nsn.nsnupdatesbackend.enums.EFollowRequestStatus;
import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.follow.FollowService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
public class FollowRequestServiceTest extends AbstractBaseTestContainer {

    @Autowired
    private FollowRequestService followRequestService;

    @Autowired
    private FollowService followService;

    @Autowired
    private AppUserService appUserService;

    private final AppUser user1 = new AppUser("nsntest1", "nsntest1", "nsntest1@test.com",
            "password");
    private final AppUser user2 = new AppUser("nsntest2", "nsntest2", "nsntest2@test.com",
            "password");

    @BeforeEach
    void setUp() {
        user2.setPrivacySetting(EPrivacySetting.FOLLOWER);
        appUserService.saveUser(user1);
    }

    @AfterEach
    void cleanUp() {
        user2.setPrivacySetting(EPrivacySetting.FOLLOWER);
    }

    @Test
    void canCreateFollowRequestIfTargetPrivacySettingIsPublic() {
        user2.setPrivacySetting(EPrivacySetting.PUBLIC);
        appUserService.saveUser(user2);

        Assertions.assertDoesNotThrow(() -> followRequestService.createFollowRequest(user1.getUsername(), user2.getUsername(),
                false));
        Assertions.assertThrows(EntityNotFoundException.class, () -> followRequestService.acceptFollowRequest(user1.getUsername(),
                user2.getUsername(), false));
        Assertions.assertThrows(EntityNotFoundException.class, () -> followRequestService.rejectFollowRequest(user1.getUsername(),
                user2.getUsername()));
    }

    @Test
    void canCreateFollowRequestIfTargetPrivacySettingIsFollowerOnly() {
        // user2 already set to follower in setup
        appUserService.saveUser(user2);

        Assertions.assertDoesNotThrow(() -> {
            followRequestService.createFollowRequest(user1.getUsername(), user2.getUsername(), false);
            followRequestService.acceptFollowRequest(user1.getUsername(), user2.getUsername(),false);
        });
    }

    @Test
    void cannotCreateFollowRequestIfTargetPrivacySettingIsPrivate()  {
        user2.setPrivacySetting(EPrivacySetting.PRIVATE);
        appUserService.saveUser(user2);

        Assertions.assertThrows(BadRequestException.class, () -> followRequestService.createFollowRequest(user1.getUsername(),
                user2.getUsername(), false));
    }

    @Test
    void cannotCreateMultiplePendingFollowRequests() {
        // user2 already set to follower in setup
        appUserService.saveUser(user2);

        Assertions.assertThrows(EntityExistsException.class, () -> {
            followRequestService.createFollowRequest(user1.getUsername(), user2.getUsername(), false);
            followRequestService.createFollowRequest(user1.getUsername(), user2.getUsername(), false);
        });
    }

    @Test
    void cannotCreateFollowRequestWhenPreviouslyDeclinedTwice() {
        // user2 already set to follower in setup
        appUserService.saveUser(user2);

        Assertions.assertDoesNotThrow(() -> {
            followRequestService.createFollowRequest(user1.getUsername(), user2.getUsername(), false);
            followRequestService.rejectFollowRequest(user1.getUsername(), user2.getUsername());

            followRequestService.createFollowRequest(user1.getUsername(), user2.getUsername(), false);
            followRequestService.rejectFollowRequest(user1.getUsername(), user2.getUsername());
        });

        Assertions.assertThrows(EntityExistsException.class, () -> {
            followRequestService.createFollowRequest(user1.getUsername(), user2.getUsername(), false);
        });

    }

    @Test
    void cannotCreateFollowRequestWhenAlreadyFollowing() {
        // user2 already set to follower in setup
        appUserService.saveUser(user2);

        Assertions.assertDoesNotThrow(() -> {
            followRequestService.createFollowRequest(user1.getUsername(), user2.getUsername(), false);
            followRequestService.acceptFollowRequest(user1.getUsername(), user2.getUsername(), false);
        });

        Assertions.assertThrows(EntityExistsException.class, () -> followRequestService.createFollowRequest(user1.getUsername(), user2.getUsername(), false));
    }

    @Test
    void cannotAcceptRequestThatDoesNotExist() {
        // user2 already set to follower in setup
        appUserService.saveUser(user2);

        Assertions.assertThrows(EntityNotFoundException.class, () -> followRequestService.acceptFollowRequest(user1.getUsername(), user2.getUsername(), false));
    }

    @Test
    void cannotRejectRequestThatDoesNotExist() {
        // user2 already set to follower in setup
        appUserService.saveUser(user2);

        Assertions.assertThrows(EntityNotFoundException.class, () -> followRequestService.rejectFollowRequest(user1.getUsername(), user2.getUsername()));
    }

    @Test
    void canSucceedWithFollowFlow() throws BadRequestException {
        // user2 already set to follower in setup
        appUserService.saveUser(user2);

        followRequestService.createFollowRequest(user1.getUsername(), user2.getUsername(), false);
        EFollowRequestStatus status = followRequestService.getStatus(user1.getUsername(), user2.getUsername());
        Assertions.assertEquals(EFollowRequestStatus.FOLLOW_PENDING, status);

        followRequestService.acceptFollowRequest(user1.getUsername(), user2.getUsername(), false);
        status = followRequestService.getStatus(user1.getUsername(), user2.getUsername());
        Assertions.assertEquals(EFollowRequestStatus.FOLLOW_TRUE, status);

        followService.unfollowFromUsername(user1.getUsername(), user2.getUsername(), false);
        status = followRequestService.getStatus(user1.getUsername(), user2.getUsername());
        Assertions.assertEquals(EFollowRequestStatus.FOLLOW_FALSE, status);
    }
}
