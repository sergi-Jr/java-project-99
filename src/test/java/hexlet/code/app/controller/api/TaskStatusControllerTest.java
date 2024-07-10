package hexlet.code.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.task.dto.TaskStatusCreateDTO;
import hexlet.code.app.task.mapper.TaskStatusMapper;
import hexlet.code.app.task.model.Task;
import hexlet.code.app.task.model.TaskStatus;
import hexlet.code.app.task.repository.TaskRepository;
import hexlet.code.app.task.repository.TaskStatusRepository;
import hexlet.code.app.user.User;
import hexlet.code.app.user.UserRepository;
import net.datafaker.Faker;
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
class TaskStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Faker faker;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    private final String defaultEmail = "hexlet@example.com";

    private User user;

    private TaskStatus status;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    void setup() {
        status = new TaskStatus();
        status.setSlug(faker.internet().slug());
        status.setName(faker.internet().username());
        statusRepository.save(status);
        user = userRepository.findByEmail(defaultEmail).get();
        token = jwt().jwt(b -> b.subject(user.getEmail()));
        status = statusRepository.findBySlug(status.getSlug()).get();
    }

    @Test
    @Order(1)
    public void testIndexWithAuth() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/task_statuses").with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    @Order(2)
    public void testIndexNoAuth() throws Exception {
        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    public void testShow() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/task_statuses/" + status.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(status.getName()),
                v -> v.node("slug").isEqualTo(status.getSlug())
        );
    }

    @Test
    @Order(4)
    public void testShowNoAuth() throws Exception {
        mockMvc.perform(get("/api/task_statuses/" + status.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    void testCreate() throws Exception {
        TaskStatusCreateDTO dto = new TaskStatusCreateDTO();
        dto.setName("test");
        dto.setSlug("test");

        var request = post("/api/task_statuses").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        Optional<TaskStatus> opActual = statusRepository.findBySlug("test");

        assertThat(opActual).isNotNull();
        TaskStatus actual = opActual.get();
        assertThat("test").isEqualTo(actual.getName());
        assertThat("test").isEqualTo(actual.getSlug());
    }


    @Test
    @Order(6)
    void testCreateNoAuth() throws Exception {
        TaskStatusCreateDTO dto = new TaskStatusCreateDTO();
        dto.setName("test");
        dto.setSlug("test");

        var request = post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    public void testUpdate() throws Exception {
        Map<String, String> data = Map.of("name", "newStatusName");

        var request = put("/api/task_statuses/" + status.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        Optional<TaskStatus> opActual = statusRepository.findById(status.getId());

        assertThat(opActual).isNotNull();
        TaskStatus actual = opActual.get();
        assertThat(data).containsEntry("name", actual.getName());
    }

    @Test
    @Order(8)
    public void testUpdateNoAuth() throws Exception {
        Map<String, String> data = Map.of("name", "newStatusName");

        var request = put("/api/task_statuses/" + status.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(9)
    public void testDelete() throws Exception {
        var request = delete("/api/task_statuses/" + status.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        TaskStatus opActual = statusRepository.findById(status.getId()).orElse(null);

        assertThat(opActual).isNull();
    }

    @Test
    @Order(10)
    public void testDeleteNoAuth() throws Exception {
        var request = delete("/api/task_statuses/" + status.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(11)
    void testDeleteHasTask() throws Exception {
        var task = new Task();
        task.setTaskStatus(status);
        task.setName("testName");
        taskRepository.save(task);

        var request = delete("/api/task_statuses/" + status.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNotAcceptable());
    }
}
