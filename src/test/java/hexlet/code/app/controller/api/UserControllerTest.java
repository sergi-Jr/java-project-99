package hexlet.code.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.task.model.Task;
import hexlet.code.app.task.repository.TaskStatusRepository;
import hexlet.code.app.user.User;
import hexlet.code.app.user.UserMapper;
import hexlet.code.app.user.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
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
    private TaskStatusRepository statusRepository;

    @Autowired
    private UserMapper userMapper;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    private User wrong;

    private User user;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor wrongToken;

    private User generateUser() {
        User generatedUser = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .ignore(Select.field(User::getTasks))
                .supply(Select.field(User::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(User::getLastName), () -> faker.name().lastName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> faker.internet().password(3, 10))
                .create();
        return generatedUser;
    }

    @BeforeEach
    void setup() {
        wrong = generateUser();
        user = generateUser();
        token = jwt().jwt(builder -> builder.subject(user.getEmail()));
        wrongToken = jwt().jwt(b -> b.subject(wrong.getEmail()));
        repository.save(user);
        repository.save(wrong);
    }

    @Test
    @Order(1)
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
    @Order(2)
    public void testIndexNoAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    public void testShow() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/users/" + user.getId()).with(token))
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
    @Order(4)
    public void testShowNoAuth() throws Exception {
        mockMvc.perform(get("/api/users/" + user.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    void testCreate() throws Exception {
        User user1 = generateUser();

        var request = post("/api/users").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userMapper.mapToCreateDTO(user1)));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        Optional<User> opActual = repository.findByEmail(user1.getEmail());

        assertThat(opActual).isNotNull();
        User actual = opActual.get();
        assertThat(user1.getEmail()).isEqualTo(actual.getEmail());
        assertThat(user1.getLastName()).isEqualTo(actual.getLastName());
        assertThat(user1.getFirstName()).isEqualTo(actual.getFirstName());
    }

    @Test
    @Order(6)
    void testCreateNoAuth() throws Exception {
        User user1 = generateUser();

        var request = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userMapper.mapToCreateDTO(user1)));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    public void testUpdate() throws Exception {
        Map<String, String> data = Map.of("email", "trueTest@gmail.com", "firstName", "John");

        var request = put("/api/users/" + user.getId()).with(token)
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
    @Order(8)
    public void testUpdateNoAuth() throws Exception {
        Map<String, String> data = Map.of("email", "trueTest@gmail.com", "firstName", "John");

        var request = put("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(9)
    public void testUpdateForbidden() throws Exception {
        Map<String, String> data = Map.of("email", "trueTest@gmail.com", "firstName", "John");

        var request = put("/api/users/" + user.getId()).with(wrongToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(10)
    public void testDelete() throws Exception {
        var request = delete("/api/users/" + user.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        User opActual = repository.findById(user.getId()).orElse(null);

        assertThat(opActual).isNull();
    }

    @Test
    @Order(11)
    public void testDeleteNoAuth() throws Exception {
        var request = delete("/api/users/" + user.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(12)
    public void testDeleteForbidden() throws Exception {
        var request = delete("/api/users/" + user.getId()).with(wrongToken);
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(13)
    void testDeleteUserHasTask() throws Exception {
        var task = new Task();
        task.setTaskStatus(statusRepository.findBySlug("published").get());
        task.setName("testName");
        user.addTask(task);
        repository.save(user);

        var request = delete("/api/users/" + user.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNotAcceptable());
    }
}
