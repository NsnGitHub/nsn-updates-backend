package com.nsn.nsnupdatesbackend.registration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void cannotRegisterUserWithIllegalUsername1() throws Exception {
        String invalidJson = """
                {
                    "username": "nsn test1",
                    "displayName": "nsn test",
                    "email": "nsntest1@gmail.com",
                    "password": "passw0rd!",
                    "ePrivacySetting": "FOLLOWER"
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
            )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Username"));
    }

    @Test
    void cannotRegisterUserWithIllegalUsername2() throws Exception {
        String invalidJson = """
                {
                    "username": "nsntest!",
                    "displayName": "nsn test",
                    "email": "nsntest1@gmail.com",
                    "password": "passw0rd!",
                    "ePrivacySetting": "FOLLOWER"
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Username"));
    }

    @Test
    void cannotRegisterUserWithIllegalUsername3() throws Exception {
        String invalidJson = """
                {
                    "username": "nsntest*($@",
                    "displayName": "nsn test",
                    "email": "nsntest1@gmail.com",
                    "password": "passw0rd!",
                    "ePrivacySetting": "FOLLOWER"
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Username"));
    }

    @Test
    void cannotRegisterUserWithNullUsername() throws Exception {
        String invalidJson = """
                {
                    "username": null,
                    "displayName": "nsn test",
                    "email": "nsntest1@gmail.com",
                    "password": "passw0rd!",
                    "ePrivacySetting": "FOLLOWER"
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Username"));
    }

    @Test
    void cannotRegisterUserWithEmptyUsername() throws Exception {
        String invalidJson = """
                {
                    "username": "",
                    "displayName": "nsn test",
                    "email": "nsntest1@gmail.com",
                    "password": "passw0rd!",
                    "ePrivacySetting": "FOLLOWER"
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Username"));
    }

    @Test
    void cannotRegisterUserWithIllegalUsername4() throws Exception {
        String invalidJson = """
                {
                    "username": "superlongtestingusernamethatisnotallowed",
                    "displayName": "nsn test",
                    "email": "nsntest1@gmail.com",
                    "password": "passw0rd!",
                    "ePrivacySetting": "FOLLOWER"
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Username"));
    }


    @Test
    void cannotRegisterUserWithIllegalPassword() throws Exception {
        String validJson = """
                {
                    "username": "nsntest1",
                    "displayName": "nsn test",
                    "email": "nsntest1@gmail.com",
                    "password": "pass w0rd!",
                    "ePrivacySetting": "FOLLOWER"
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Password"));
    }

    @Test
    void cannotRegisterUserWithNullPassword() throws Exception {
        String validJson = """
                {
                    "username": "nsntest1",
                    "displayName": "nsn test",
                    "email": "nsntest1@gmail.com",
                    "password": null,
                    "ePrivacySetting": "FOLLOWER"
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Password"));
    }

    @Test
    void cannotRegisterUserWithEmptyPassword() throws Exception {
        String validJson = """
                {
                    "username": "nsntest1",
                    "displayName": "nsn test",
                    "email": "nsntest1@gmail.com",
                    "password": "",
                    "ePrivacySetting": "FOLLOWER"
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Password"));
    }

    @Test
    void cannotRegisterUserWithIllegalEmail() throws Exception {
        String validJson = """
                {
                    "username": "nsntest1",
                    "displayName": "Nsn Test",
                    "email": "nsntest1notanemail",
                    "password": "passw0rd!",
                    "ePrivacySetting": "FOLLOWER"
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson)
        )
            .andExpect(status().isBadRequest())
            .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Email"));
    }

    @Test
    void canRegisterUser() throws Exception {
        String validJson = """
                {
                    "username": "nsntest1",
                    "displayName": "Nsn Test",
                    "email": "nsntest1@gmail.com",
                    "password": "passw0rd!",
                    "ePrivacySetting": "FOLLOWER"
                }
                """;

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validJson)
        )
            .andExpect(status().isCreated());
    }

    @Test
    void cannotExcludePrivacySetting() throws Exception {
        String invalidJson = """
                {
                    "username": "nsntest1",
                    "displayName": "Nsn Test",
                    "email": "nsntest1@gmail.com",
                    "password": "passw0rd!"
                }
                """;

        String response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
        )
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString();

        assertTrue(response.contains("Privacy setting must be set"));
    }
}
