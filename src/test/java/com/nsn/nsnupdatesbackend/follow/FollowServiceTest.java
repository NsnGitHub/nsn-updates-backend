package com.nsn.nsnupdatesbackend.follow;

import com.nsn.nsnupdatesbackend.AbstractBaseTestContainer;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
public class FollowServiceTest extends AbstractBaseTestContainer {

    @Autowired
    private FollowService followService;

    private static final AppUser user1 = new AppUser("nsntest1", "nsntest1", "nsntest1@test.com", "password");
    private static final AppUser user2 = new AppUser("nsntest2", "nsntest2", "nsntest2@test.com", "password");

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
    void canFollowFromUsernames() {
        followService.followFromUsername(user1.getUsername(), user2.getUsername(), false);
        assertEquals(1, followService.getFollowersDtoForUsername(user2.getUsername()).size());
        assertEquals(1, followService.getFollowingDtoForUsername(user1.getUsername()).size());
    }

    @Test
    void canFollowFromAppUserObjects() {
        followService.followFromAppUser(user1, user2, false);
        assertEquals(1, followService.getFollowersDtoForUsername(user2.getUsername()).size());
        assertEquals(1, followService.getFollowingDtoForUsername(user1.getUsername()).size());
    }

    @Test
    void canUnfollowFromUsernames() {
        followService.followFromAppUser(user1, user2, false);
        assertEquals(1, followService.getFollowersDtoForUsername(user2.getUsername()).size());
        assertEquals(1, followService.getFollowingDtoForUsername(user1.getUsername()).size());

        followService.unfollowFromUsername(user1.getUsername(), user2.getUsername(), false);
        assertEquals(0, followService.getFollowersDtoForUsername(user2.getUsername()).size());
        assertEquals(0, followService.getFollowingDtoForUsername(user1.getUsername()).size());
    }

    @Test
    void canUnfollowFromAppUserObjects() {
        followService.followFromAppUser(user1, user2, false);
        assertEquals(1, followService.getFollowersDtoForUsername(user2.getUsername()).size());
        assertEquals(1, followService.getFollowingDtoForUsername(user1.getUsername()).size());
        assertEquals(1, followService.getFollowersForUser(user2).size());


        followService.unfollowFromAppUser(user1, user2);
        assertEquals(0, followService.getFollowersDtoForUsername(user2.getUsername()).size());
        assertEquals(0, followService.getFollowingDtoForUsername(user1.getUsername()).size());
        assertEquals(0, followService.getFollowersForUser(user2).size());

    }
}
