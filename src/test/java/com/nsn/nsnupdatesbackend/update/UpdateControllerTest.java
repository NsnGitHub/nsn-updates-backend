package com.nsn.nsnupdatesbackend.update;

import com.nsn.nsnupdatesbackend.AbstractBaseTestContainer;
import com.nsn.nsnupdatesbackend.enums.EJwtToken;
import com.nsn.nsnupdatesbackend.enums.EUserRole;
import com.nsn.nsnupdatesbackend.user.AppUser;
import com.nsn.nsnupdatesbackend.user.AppUserService;
import com.nsn.nsnupdatesbackend.utils.JWTUtils;
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
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UpdateControllerTest extends AbstractBaseTestContainer {

    @Autowired
    private MockMvc mockMvc;

    private static String jwtToken;

    @BeforeAll
    static void setUp(@Autowired JWTUtils jwtUtils, @Autowired AppUserService appUserService) {
        AppUser user = new AppUser("nsntest1", "nsntest1", "nsntest1@test.com", "password");
        appUserService.saveUser(user);

        jwtToken = jwtUtils.createToken("nsntest1", EJwtToken.ACCESS_TOKEN, EUserRole.ROLE_USER);
    }

    @AfterAll
    static void cleanUp(@Autowired AppUserService appUserService) {
        try {
            AppUser userToDelete = appUserService.getUserByUsername("nsntest1");
            appUserService.deleteUser(userToDelete);
        } catch (Exception ignored) {
            // User doesn't exist
        }

    }

    @Test
    void canCreateUpdate() throws Exception {
        String content = "Hello this is a test post.";
        String validJson = """
                {
                    "content": "%s"
                }
                """.formatted(content);

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/update/create")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson)
        )
            .andExpect(status().isCreated());
    }

    @Test
    void cannotCreateUpdateWithNullContent() throws Exception {
        String invalidJson = """
                {
                    "content": null
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/update/create")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        System.out.println(response);
        assertTrue(response.contains("null"));
    }

    @Test
    void cannotCreateUpdateWithEmptyContent() throws Exception {
        String invalidJson = """
                {
                    "content": ""
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/update/create")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("1 and 1000 characters"));
    }

    @Test
    void cannotCreateUpdateWithContentOverCharacterLimit() throws Exception {
        char[] charArray = new char[1001];
        Arrays.fill(charArray, 'a');
        String content = new String(charArray);

        String invalidJson = """
                {
                    "content": "%s"
                }
                """.formatted(content);

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/update/create")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("1 and 1000 characters"));
    }

}
