package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.AbstractBaseTestContainer;
import com.nsn.nsnupdatesbackend.follow.FollowService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class UpdateServiceTest extends AbstractBaseTestContainer {

    @Autowired
    private UpdateService updateService;

    /**
     * Creating objects here to be used in multiple tests.
     */

    private static final AppUser user1 = new AppUser("nsntest1", "nsntest1", "nsntest1@test.com", "password");
    private static final AppUser user2 = new AppUser("nsntest2", "nsntest2", "nsntest2@test.com", "password");

    /**
     * Start of tests
     */

    @BeforeAll
    static void setUp(@Autowired AppUserService appUserService, @Autowired FollowService followService) {
        appUserService.saveUser(user1);
        appUserService.saveUser(user2);
        followService.followFromAppUser(user1, user2, false);
    }

    @AfterAll
    static void cleanUp(@Autowired AppUserService appUserService, @Autowired FollowService followService) {
        followService.unfollowFromAppUser(user1, user2);
        appUserService.deleteUser(user1);
        appUserService.deleteUser(user2);
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
        String content = "Hello this is a test post.";
        UpdatePostReqDto updatePostReqDto = new UpdatePostReqDto(content);
        updateService.createPost(user2.getUsername(), updatePostReqDto);

        // User should receive this in their inbox;
        assertEquals(1, updateService.getUpdatesFromInboxByUsername(user1.getUsername()).size());
    }

    @Test
    void canPostedUpdatesBeReceivedByFollowersWithPagination() {
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

    @Test
    void canSaveUpdateEdits() {
        String content = "Hello this is a test post.";
        UpdatePostReqDto updatePostReqDto = new UpdatePostReqDto(content);
        UpdateDto createdUpdate = updateService.createPost(user1.getUsername(), updatePostReqDto);

        assertFalse(createdUpdate.isEdited());

        String newContent = "Hello this is an edited post.";
        UpdatePostReqDto updatePostReqDtoForEdited = new UpdatePostReqDto(newContent);
        UpdateDto editedUpdate = updateService.editUpdate(createdUpdate, updatePostReqDtoForEdited);

        assertEquals(createdUpdate.id(), editedUpdate.id());
        assertEquals(newContent, editedUpdate.content());
        assertTrue(editedUpdate.isEdited());
    }

    @Test
    void canDeleteUpdate() {
        String content = "Hello this is a test post.";
        UpdatePostReqDto updatePostReqDto = new UpdatePostReqDto(content);
        UpdateDto createdUpdate = updateService.createPost(user1.getUsername(), updatePostReqDto);

        assertDoesNotThrow(() -> updateService.deleteUpdateById(createdUpdate.id()));
        assertThrows(EntityNotFoundException.class, () -> updateService.getUpdateById(createdUpdate.id()));
    }
}
