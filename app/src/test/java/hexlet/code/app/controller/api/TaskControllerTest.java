package hexlet.code.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.label.LabelRepository;
import hexlet.code.app.task.dto.TaskCreateDTO;
import hexlet.code.app.task.mapper.TaskMapper;
import hexlet.code.app.task.model.Task;
import hexlet.code.app.task.repository.TaskRepository;
import hexlet.code.app.task.repository.TaskStatusRepository;
import hexlet.code.app.user.User;
import hexlet.code.app.user.UserRepository;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
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
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private TaskMapper userMapper;

    private User wrong;

    private User user;

    private Task task;

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

    private Task generateTask() {
        Task generatedTask = Instancio.of(Task.class)
                .ignore(Select.field(Task::getId))
                .ignore(Select.field(Task::getCreatedAt))
                .ignore(Select.field(Task::getAssignee))
                .ignore(Select.field(Task::getTaskStatus))
                .ignore(Select.field(Task::getLabels))
                .supply(Select.field(Task::getName), () -> faker.name().name())
                .create();

        return generatedTask;
    }

    @BeforeEach
    void setup() {
        repository.deleteAll();

        wrong = generateUser();
        user = generateUser();
        userRepository.save(user);
        userRepository.save(wrong);

        task = generateTask();
        var ts = statusRepository.findBySlug("draft").get();
        task.setTaskStatus(ts);
        var l = labelRepository.findByName("bug").get();
        task.addLabel(l);
        repository.save(task);
    }

    @Test
    public void testIndexWithAuth() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks").with(user(user)))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    public void testIndexNoAuth() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShow() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks/" + task.getId()).with(user(user)))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(task.getName()),
                v -> v.node("status").isEqualTo(task.getTaskStatus().getSlug())
        );
    }

    @Test
    public void testShowNoAuth() throws Exception {
        mockMvc.perform(get("/api/tasks/" + task.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreate() throws Exception {
        TaskCreateDTO dto = new TaskCreateDTO();
        dto.setTitle("title");
        dto.setStatus("testStatus");
        dto.setAssigneeId(JsonNullable.of(user.getId()));

        var request = post("/api/tasks").with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateNoAuth() throws Exception {
        TaskCreateDTO dto = new TaskCreateDTO();
        dto.setTitle("title");
        dto.setStatus("testStatus");

        var request = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdate() throws Exception {
        Map<String, String> data = Map.of("index", "100", "title", "newName");
        user.addTask(task);
        userRepository.save(user);

        var request = put("/api/tasks/" + task.getId()).with(user(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        Optional<Task> opActual = repository.findById(task.getId());

        assertThat(opActual).isNotNull();
        Task actual = opActual.get();
        assertThat(data).containsEntry("index", String.valueOf(actual.getIndex()));
        assertThat(data).containsEntry("title", actual.getName());
    }

    @Test
    public void testUpdateNoAuth() throws Exception {
        Map<String, String> data = Map.of("index", "100", "name", "newName");

        var request = put("/api/tasks/" + task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUpdateForbidden() throws Exception {
        Map<String, String> data = Map.of("index", "100", "name", "newName");

        var request = put("/api/tasks/" + task.getId()).with(user(wrong))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDelete() throws Exception {
        user.addTask(task);
        userRepository.save(user);

        var request = delete("/api/tasks/" + task.getId()).with(user(user));
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        Task opActual = repository.findById(task.getId()).orElse(null);

        assertThat(opActual).isNull();
    }

    @Test
    public void testDeleteNoAuth() throws Exception {
        var request = delete("/api/tasks/" + task.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteForbidden() throws Exception {
        var request = delete("/api/tasks/" + task.getId()).with(user(wrong));
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }
}
