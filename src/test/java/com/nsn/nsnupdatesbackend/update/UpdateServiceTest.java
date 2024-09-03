package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.AbstractBaseTestContainer;
import com.nsn.nsnupdatesbackend.follow.FollowService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
public class UpdateServiceTest extends AbstractBaseTestContainer {

    @Autowired
    private UpdateService updateService;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private FollowService followService;

    /**
     * Creating objects here to be used in multiple tests.
     */

    AppUser user1 = new AppUser("nsntest1", "nsntest1", "nsntest1@test.com", "password");
    AppUser user2 = new AppUser("nsntest2", "nsntest2", "nsntest2@test.com", "password");

    /**
     * Start of tests
     */

    @BeforeEach
    void setUp() {
        appUserService.saveUser(user1);
        appUserService.saveUser(user2);
    }

    @Test
    void canCreateUpdate() {
        String content = "Hello this is a test post.";
        UpdatePostReqDto updatePostReqDto = new UpdatePostReqDto(content);

        updateService.createPost("nsntest1", updatePostReqDto);
        updateService.createPost("nsntest1", updatePostReqDto);
        updateService.createPost("nsntest2", updatePostReqDto);

        assertEquals(3, updateService.getAllUpdates().size());
    }

    @Test
    void canPostedUpdatesBeReceivedByFollowers() {
        // Create an entry where user1 IS FOLLOWING user2
        followService.followFromAppUser(user1, user2);

        String content = "Hello this is a test post.";
        UpdatePostReqDto updatePostReqDto = new UpdatePostReqDto(content);
        updateService.createPost(user2.getUsername(), updatePostReqDto);

        // User should receive this in their inbox;
        assertEquals(1, updateService.getUpdatesFromInboxByUsername(user1.getUsername()).size());
    }

    @Test
    void canPostedUpdatesBeReceivedByFollowersWithPagination() {
        // Create an entry where user1 IS FOLLOWING user2
        followService.followFromAppUser(user1, user2);

        String content = "Hello this is a test post.";
        UpdatePostReqDto updatePostReqDto = new UpdatePostReqDto(content);

        // Create 5 Posts
        updateService.createPost(user2.getUsername(), updatePostReqDto);
        updateService.createPost(user2.getUsername(), updatePostReqDto);
        updateService.createPost(user2.getUsername(), updatePostReqDto);
        updateService.createPost(user2.getUsername(), updatePostReqDto);
        updateService.createPost(user2.getUsername(), updatePostReqDto);

        // User should receive this in their inbox;
        final int PAGE = 0;

        assertEquals(1, updateService.getUpdatesFromInboxByUsernamePaginated(PAGE, 1, user1.getUsername()).size());
        assertEquals(2, updateService.getUpdatesFromInboxByUsernamePaginated(PAGE, 2, user1.getUsername()).size());
        assertEquals(3, updateService.getUpdatesFromInboxByUsernamePaginated(PAGE, 3, user1.getUsername()).size());
        assertEquals(4, updateService.getUpdatesFromInboxByUsernamePaginated(PAGE, 4, user1.getUsername()).size());
        assertEquals(5, updateService.getUpdatesFromInboxByUsernamePaginated(PAGE, 5, user1.getUsername()).size());

    }
}
