package com.nsn.nsnupdatesbackend.user;

import com.nsn.nsnupdatesbackend.AbstractBaseTestContainer;
import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.registration.RegistrationRequestDto;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

        Assertions.assertDoesNotThrow(() -> {
            appUserService.getUserByUsername("nsntest1");
        });
    }

    @Test
    void cannotRegisterUserWithDuplicateUsername() {
        appUserService.registerUser(request1);

        RegistrationRequestDto requestWithDuplicateUsername = new RegistrationRequestDto(
                "nsntest1", "nsntest2", "nsntest2@test.com", "password", EPrivacySetting.FOLLOWER
        );

        Assertions.assertThrows(EntityExistsException.class, () -> appUserService.registerUser(requestWithDuplicateUsername));
    }

    @Test
    void cannotRegisterUserWithDuplicateEmail() {
        appUserService.registerUser(request1);

        RegistrationRequestDto requestWithDuplicateEmail = new RegistrationRequestDto(
                "nsntest2", "nsntest2", "nsntest1@test.com", "password", EPrivacySetting.FOLLOWER
        );

        Assertions.assertThrows(EntityExistsException.class, () -> appUserService.registerUser(requestWithDuplicateEmail));
    }

    @Test
    void canDeleteUser() {
        appUserService.registerUser(request1);

        Assertions.assertDoesNotThrow(() -> {
            AppUser user = appUserService.getUserByUsername("nsntest1");
            appUserService.deleteUser(user);
        });

        Assertions.assertThrows(EntityNotFoundException.class, () -> appUserService.getUserByUsername("nsntest1"));
    }

    @Test
    void canGetAllUsers() {
        Assertions.assertTrue(appUserService.getAllUsers().isEmpty());

        appUserService.registerUser(request1);
        appUserService.registerUser(request2);

        Assertions.assertEquals(2, appUserService.getAllUsers().size());
    }

    @Test
    void canGetUserDto() {
        appUserService.registerUser(request1);

        AppUser user = appUserService.getUserByUsername("nsntest1");
        AppUserDto userDto = appUserService.getUserDtoByUsername("nsntest1");

        Assertions.assertEquals(user.getUsername(), userDto.username());
        Assertions.assertEquals(user.getDisplayName(), userDto.displayName());
        Assertions.assertEquals(user.getBio(), userDto.bio());
        Assertions.assertEquals(user.getPrivacySetting(), userDto.privacySetting());

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

        Assertions.assertEquals(user.getDisplayName(), updatedUser.getDisplayName());
        Assertions.assertEquals(user.getBio(), updatedUser.getBio());
        Assertions.assertEquals(user.getEmail(), updatedUser.getEmail());
        Assertions.assertEquals(user.getPrivacySetting(), updatedUser.getPrivacySetting());
    }

}
