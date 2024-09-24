package com.nsn.nsnupdatesbackend.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AppUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

//    @Test
//    void canLogin() throws Exception {
//        String content = "Hello this is a test post.";
//        String validJson = """
//                {
//                    "content": "%s"
//                }
//                """.formatted(content);
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.post("/api/v1/update/create")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(validJson)
//                )
//                .andExpect(status().isCreated());
//    }

}
