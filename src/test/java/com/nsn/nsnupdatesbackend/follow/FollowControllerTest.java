package com.nsn.nsnupdatesbackend.follow;

import com.nsn.nsnupdatesbackend.enums.EFollowRequestStatus;
import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import com.nsn.nsnupdatesbackend.enums.EUserRole;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final AppUser user1 = new AppUser("nsntest1", "nsntest1", "nsntest1@test.com", "password");
    private static final AppUser user2 = new AppUser("nsntest2", "nsntest2", "nsntest2@test.com", "password");
    private static final AppUser user3 = new AppUser("nsntest3", "nsntest3", "nsntest3@test.com", "password");


    private static String jwtTokenForUser1;

    @BeforeAll
    public static void setUp(@Autowired AppUserService appUserService, @Autowired FollowService followService, @Autowired JWTUtils jwtUtils) {
        appUserService.saveUser(user1);
        appUserService.saveUser(user2);
        appUserService.saveUser(user3);

        followService.followFromAppUser(user1, user2, false);

        jwtTokenForUser1 = jwtUtils.createToken(user1.getUsername(), EJwtToken.ACCESS_TOKEN, EUserRole.ROLE_USER);
    }

    @AfterAll
    public static void cleanUp(@Autowired AppUserService appUserService, @Autowired FollowService followService) {
        followService.unfollowFromAppUser(user1, user2);
        appUserService.deleteUser(user1);
        appUserService.deleteUser(user2);
    }

    @Test
    void canGetCorrectFollowStatus1() throws Exception {
        String response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/follow/request/status/nsntest2")
                .header("Authorization", "Bearer " + jwtTokenForUser1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        Assertions.assertTrue(response.contains(EFollowRequestStatus.FOLLOW_TRUE.toString()));
    }

    @Test
    void canGetCorrectFollowStatus2() throws Exception {
        String response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/follow/request/status/nsntest3")
                .header("Authorization", "Bearer " + jwtTokenForUser1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        Assertions.assertTrue(response.contains(EFollowRequestStatus.FOLLOW_FALSE.toString()));
    }

    @Test
    void canGetCorrectFollowStatusFromNonExistentUser() throws Exception {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/follow/request/status/nsntest4")
                .header("Authorization", "Bearer " + jwtTokenForUser1)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest());
    }


}
