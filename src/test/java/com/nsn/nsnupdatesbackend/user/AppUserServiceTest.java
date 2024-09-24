package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.AbstractBaseTestContainer;
import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.registration.RegistrationRequestDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@Transactional // Using transactional for test isolation
public class AppUserServiceTest extends AbstractBaseTestContainer {

    @Autowired
    private AppUserService appUserService;

    /**
     * Creating objects here to be used in multiple tests.
     */

    RegistrationRequestDto request1 = new RegistrationRequestDto(
            "nsntest1", "nsntest1", "nsntest1@test.com", "password", EPrivacySetting.FOLLOWER
    );

    RegistrationRequestDto request2 = new RegistrationRequestDto(
            "nsntest2", "nsntest2", "nsntest2@test.com", "password", EPrivacySetting.FOLLOWER
    );


    /**
     * Start of tests
     */

    @Test
    void canRegisterUser() {
        appUserService.registerUser(request1);

        assertDoesNotThrow(() -> {
            appUserService.getUserByUsername("nsntest1");
        });
    }

    @Test
    void cannotRegisterUserWithDuplicateUsername() {
        appUserService.registerUser(request1);

        RegistrationRequestDto requestWithDuplicateUsername = new RegistrationRequestDto(
                "nsntest1", "nsntest2", "nsntest2@test.com", "password", EPrivacySetting.FOLLOWER
        );

        assertThrows(EntityExistsException.class, () -> appUserService.registerUser(requestWithDuplicateUsername));
    }

    @Test
    void cannotRegisterUserWithDuplicateEmail() {
        appUserService.registerUser(request1);

        RegistrationRequestDto requestWithDuplicateEmail = new RegistrationRequestDto(
                "nsntest2", "nsntest2", "nsntest1@test.com", "password", EPrivacySetting.FOLLOWER
        );

        assertThrows(EntityExistsException.class, () -> appUserService.registerUser(requestWithDuplicateEmail));
    }

    @Test
    void canDeleteUser() {
        appUserService.registerUser(request1);

        assertDoesNotThrow(() -> {
            AppUser user = appUserService.getUserByUsername("nsntest1");
            appUserService.deleteUser(user);
        });

        assertThrows(EntityNotFoundException.class, () -> appUserService.getUserByUsername("nsntest1"));
    }

    @Test
    void canGetAllUsers() {
        assertTrue(appUserService.getAllUsers().isEmpty());

        appUserService.registerUser(request1);
        appUserService.registerUser(request2);

        assertEquals(2, appUserService.getAllUsers().size());
    }

    @Test
    void canGetUserDto() {
        appUserService.registerUser(request1);

        AppUser user = appUserService.getUserByUsername("nsntest1");
        AppUserDto userDto = appUserService.getUserDtoByUsername("nsntest1");

        assertEquals(user.getUsername(), userDto.username());
        assertEquals(user.getDisplayName(), userDto.displayName());
        assertEquals(user.getBio(), userDto.bio());
        assertEquals(user.getPrivacySetting(), userDto.privacySetting());

    }

    @Test
    void canUpdateUser() {
        AppUser user = new AppUser("nsnupdatetest1", "nsntest1", "nsntest1@test.com", "password");
        appUserService.saveUser(user);

        user.setDisplayName("nsnupdatetest1");
        user.setBio("New Bio");
        user.setEmail("nsnupdatetest1@test.com");
        user.setPrivacySetting(EPrivacySetting.PRIVATE);

        appUserService.saveUser(user);

        AppUser updatedUser = appUserService.getUserByUsername("nsnupdatetest1");

        assertEquals(user.getDisplayName(), updatedUser.getDisplayName());
        assertEquals(user.getBio(), updatedUser.getBio());
        assertEquals(user.getEmail(), updatedUser.getEmail());
        assertEquals(user.getPrivacySetting(), updatedUser.getPrivacySetting());
    }

}
