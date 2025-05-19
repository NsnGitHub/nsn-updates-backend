package com.nsn.nsnupdatesbackend.like;

import com.nsn.nsnupdatesbackend.AbstractBaseTestContainer;
import com.nsn.nsnupdatesbackend.update.Update;
import com.nsn.nsnupdatesbackend.update.UpdateService;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

@Transactional
public class LikeServiceTest extends AbstractBaseTestContainer {

    @Autowired
    private LikeService likeService;

    private static final AppUser user1 = new AppUser("nsntest1", "nsntest1", "nsntest1@test.com", "password");
    private static final AppUser user2 = new AppUser("nsntest2", "nsntest2", "nsntest2@test.com", "password");
    private static final Update update = new Update("Test post", user1 );

    @BeforeAll
    public static void setUp(@Autowired AppUserService appUserService, @Autowired UpdateService updateService) {
        appUserService.saveUser(user1);
        appUserService.saveUser(user2);
        updateService.saveUpdate(update);
    }

    @AfterAll
    public static void cleanUp(@Autowired AppUserService appUserService, @Autowired UpdateService updateService) {
        try {
            updateService.deleteUpdate(update);
            appUserService.deleteUser(user1);
            appUserService.deleteUser(user2);
        } catch (Exception ignored) {
            // User(s) don't exist
            // Update doesn't exist
        }
    }

    @Test
    void canGetLikeById() {
        LikeDto createdLike = likeService.like(user2.getUsername(), update.getId());
        Assertions.assertEquals(createdLike, likeService.getLikeById(createdLike.id()));
    }

    @Test
    void canLike() {
        Assertions.assertDoesNotThrow(() -> likeService.like(user2.getUsername(), update.getId()));
    }

    @Test
    void canUnlike() {
        Assertions.assertDoesNotThrow(() -> {
            likeService.like(user2.getUsername(), update.getId());
            likeService.unlike(user2.getUsername(), update.getId());
        });
    }

    @Test
    void canDetectUpdateIsLikedByUser() {
        Assertions.assertThrows(EntityExistsException.class, () -> {
            likeService.like(user2.getUsername(), update.getId());
            likeService.like(user2.getUsername(), update.getId());
        });
    }

    @Test
    void canDetectUpdateIsCreatedByUser() {
        Assertions.assertThrows(AccessDeniedException.class, () -> {
            likeService.like(user1.getUsername(), update.getId());
        });
    }

}
