package hexlet.code.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.role.type.UserRoleType;
import hexlet.code.app.user.User;
import hexlet.code.app.user.UserMapper;
import hexlet.code.app.user.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper userMapper;

    private User generateUser() {
        User user = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .ignore(Select.field(User::getUserRoles))
                .ignore(Select.field(User::getTaskRoles))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> faker.internet().password(3, 10))
                .create();
        user.addUserRole(UserRoleType.ADMIN);
        return user;
    }

    @Test
    public void testIndexWithAuth() throws Exception {
        User user1 = generateUser();
        User user2 = generateUser();
        repository.save(user1);
        repository.save(user2);

        MvcResult result = mockMvc.perform(get("/api/users").with(user(user1)))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testIndexNoAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testIndexForbidden() throws Exception {
        User wrong = generateUser();
        wrong.removeUserRole(UserRoleType.ADMIN);
        mockMvc.perform(get("/api/users").with(user(wrong)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testShow() throws Exception {
        User user = generateUser();
        repository.save(user);

        MvcResult result = mockMvc.perform(get("/api/users/" + user.getId()).with(user(user)))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("email").isEqualTo(user.getEmail()),
                v -> v.node("firstName").isEqualTo(user.getFirstName()),
                v -> v.node("lastName").isEqualTo(user.getLastName())
        );
    }

    @Test
    public void testShowNoAuth() throws Exception {
        User user = generateUser();
        repository.save(user);

        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShowForbidden() throws Exception {
        User user = generateUser();
        User wrong = generateUser();
        wrong.removeUserRole(UserRoleType.ADMIN);
        repository.save(user);
        repository.save(wrong);

        mockMvc.perform(get("/api/users/" + user.getId()).with(user(wrong)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreate() throws Exception {
        User user = generateUser();
        User admin = generateUser();
        repository.save(admin);

        var request = post("/api/users").with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userMapper.mapToCreateDTO(user)));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        Optional<User> opActual = repository.findByEmail(user.getEmail());

        assertThat(opActual).isNotNull();
        User actual = opActual.get();
        assertThat(user.getEmail()).isEqualTo(actual.getEmail());
        assertThat(user.getLastName()).isEqualTo(actual.getLastName());
        assertThat(user.getFirstName()).isEqualTo(actual.getFirstName());
    }

    @Test
    void testCreateForbidden() throws Exception {
        User user = generateUser();
        User wrong = generateUser();
        wrong.removeUserRole(UserRoleType.ADMIN);
        repository.save(wrong);

        var request = post("/api/users").with(user(wrong))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userMapper.mapToCreateDTO(user)));
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateNoAuth() throws Exception {
        User user = generateUser();

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userMapper.mapToCreateDTO(user)));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdate() throws Exception {
        User user = generateUser();
        repository.save(user);
        Map<String, String> data = Map.of("email", "trueTest@gmail.com", "firstName", "John");

        var request = put("/api/users/" + user.getId()).with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        Optional<User> opActual = repository.findById(user.getId());

        assertThat(opActual).isNotNull();
        User actual = opActual.get();
        assertThat(data).containsEntry("email", actual.getEmail());
        assertThat(user.getLastName()).isEqualTo(actual.getLastName());
        assertThat(data).containsEntry("firstName", actual.getFirstName());
    }

    @Test
    public void testUpdateNoAuth() throws Exception {
        User user = generateUser();
        repository.save(user);
        Map<String, String> data = Map.of("email", "trueTest@gmail.com", "firstName", "John");

        var request = put("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateForbidden() throws Exception {
        User user = generateUser();
        User wrong = generateUser();
        repository.save(wrong);
        repository.save(user);
        Map<String, String> data = Map.of("email", "trueTest@gmail.com", "firstName", "John");

        var request = put("/api/users/" + user.getId()).with(user(wrong))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDelete() throws Exception {
        User user = generateUser();
        repository.save(user);

        var request = delete("/api/users/" + user.getId()).with(user(user));
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        User opActual = repository.findById(user.getId()).orElse(null);

        assertThat(opActual).isNull();
    }

    @Test
    public void testDeleteNoAuth() throws Exception {
        User user = generateUser();
        repository.save(user);

        var request = delete("/api/users/" + user.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteForbidden() throws Exception {
        User user = generateUser();
        User wrong = generateUser();
        repository.save(wrong);
        repository.save(user);

        var request = delete("/api/users/" + user.getId()).with(user(wrong));
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }
}
