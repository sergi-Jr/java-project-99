package hexlet.code.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.authentication.model.AuthRequest;
import hexlet.code.app.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private UserRepository repository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private AuthRequest generateAuthRequest(String email, String password) {
        AuthRequest request = new AuthRequest();
        request.setUsername(email);
        request.setPassword(password);
        return request;
    }

    @Test
    public void testLoginSuccess() throws Exception {
        AuthRequest requestBody = generateAuthRequest("hexlet@example.com", "qwerty");

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody));
        var response = mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andReturn();
        var body = response.getResponse().getContentAsString();

        assertThat(body).isNotNull();
    }

    @Test
    public void testLoginFailure() throws Exception {
        AuthRequest requestBody = generateAuthRequest("wrongEmail@gmail.com", "password");

        var request = post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(requestBody));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
