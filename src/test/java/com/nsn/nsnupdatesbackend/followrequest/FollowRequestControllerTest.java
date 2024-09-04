package com.nsn.nsnupdatesbackend.followrequest;

import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import com.nsn.nsnupdatesbackend.enums.EPrivacySetting;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserRepository;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
import jakarta.transaction.Transactional;
import org.junit.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional //Needs transactional here to not add data entries into Notification table.
public class FollowRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final AppUser user1 = new AppUser("nsntest1", "nsntest1", "nsntest1@test.com", "password");
    private static final AppUser user2 = new AppUser("nsntest2", "nsntest2", "nsntest2@test.com", "password");

    private static String jwtTokenForUser1;
    private static String jwtTokenForUser2;

    private static final String illegalUsername1 = "aaaaaaaaaaaaaaaa";
    private static final String illegalUsername2 = "aa";

    @BeforeAll
    public static void setUp(@Autowired AppUserService appUserService, @Autowired JWTUtils jwtUtils) {
        appUserService.saveUser(user1);
        appUserService.saveUser(user2);

        jwtTokenForUser1 = jwtUtils.createToken(user1.getUsername(), EJwtToken.ACCESS_TOKEN);
        jwtTokenForUser2 = jwtUtils.createToken(user2.getUsername(), EJwtToken.ACCESS_TOKEN);
    }

    @AfterAll
    public static void cleanUp(@Autowired AppUserService appUserService) {
        appUserService.deleteUser(user1);
        appUserService.deleteUser(user2);
    }

    @Test
    void canCreateFollowRequestWithValidDto() throws Exception {
        String validJson = """
                {
                    "targetUsername": "%s"
                }
                """.formatted(user2.getUsername());

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/follow/request")
                .header("Authorization", "Bearer " + jwtTokenForUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson)
        )
            .andExpect(status().isOk());
    }

    @Test
    void canAcceptFollowRequestWithValidDto() throws Exception {
        String validJsonToCreateFollowRequest = """
                {
                    "targetUsername": "%s"
                }
                """.formatted(user2.getUsername());

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/follow/request")
                .header("Authorization", "Bearer " + jwtTokenForUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJsonToCreateFollowRequest)
        )
            .andExpect(status().isOk());

        String validJsonToAcceptFollowRequest = """
                {
                    "requesterUsername": "%s"
                }
                """.formatted(user1.getUsername());

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/follow/request/accept")
                .header("Authorization", "Bearer " + jwtTokenForUser2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJsonToAcceptFollowRequest)
        )
            .andExpect(status().isOk());
    }

    @Test
    void canRejectFollowRequestWithValidDto() throws Exception {
        String validJsonToCreateFollowRequest = """
                {
                    "targetUsername": "%s"
                }
                """.formatted(user2.getUsername());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/follow/request")
                                .header("Authorization", "Bearer " + jwtTokenForUser1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validJsonToCreateFollowRequest)
                )
                .andExpect(status().isOk());

        String validJsonToAcceptFollowRequest = """
                {
                    "requesterUsername": "%s"
                }
                """.formatted(user1.getUsername());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/follow/request/reject")
                                .header("Authorization", "Bearer " + jwtTokenForUser2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validJsonToAcceptFollowRequest)
                )
                .andExpect(status().isOk());
    }

    @Test
    void cannotCreateFollowRequestWithInvalidDto() throws Exception {
        String validJson = """
                {
                    "targetUsername": "%s"
                }
                """.formatted(illegalUsername1);

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/follow/request")
                .header("Authorization", "Bearer " + jwtTokenForUser1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Username"));
    }

    @Test
    void cannotAcceptFollowRequestWithInvalidDto() throws Exception {
        String validJsonToCreateFollowRequest = """
                {
                    "targetUsername": "%s"
                }
                """.formatted(user2.getUsername());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/follow/request")
                                .header("Authorization", "Bearer " + jwtTokenForUser1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validJsonToCreateFollowRequest)
                )
                .andExpect(status().isOk());

        String validJsonToAcceptFollowRequest = """
                {
                    "requesterUsername": "%s"
                }
                """.formatted(illegalUsername1);

        String acceptResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/follow/request/accept")
                .header("Authorization", "Bearer " + jwtTokenForUser2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJsonToAcceptFollowRequest)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(acceptResponse.contains("Username"));
    }

    @Test
    void cannotRejectFollowRequestWithInvalidDto() throws Exception {
        String validJsonToCreateFollowRequest = """
                {
                    "targetUsername": "%s"
                }
                """.formatted(user2.getUsername());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/follow/request")
                                .header("Authorization", "Bearer " + jwtTokenForUser1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validJsonToCreateFollowRequest)
                )
                .andExpect(status().isOk());

        String validJsonToAcceptFollowRequest = """
                {
                    "requesterUsername": "%s"
                }
                """.formatted(illegalUsername2);

        String rejectResponse = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/follow/request/reject")
                .header("Authorization", "Bearer " + jwtTokenForUser2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJsonToAcceptFollowRequest)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(rejectResponse.contains("Username"));
    }
}
