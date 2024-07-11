package hexlet.code.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.label.Label;
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
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
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

    private Label label;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

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
        token = jwt().jwt(b -> b.subject(user.getEmail()));
        wrongToken = jwt().jwt(b -> b.subject(wrong.getEmail()));

        userRepository.save(user);
        userRepository.save(wrong);

        task = generateTask();
        var ts = statusRepository.findBySlug("draft").get();
        task.setTaskStatus(ts);

        label = new Label();
        label.setName(faker.lorem().word() + faker.number().randomNumber());
        labelRepository.save(label);
        task.addLabel(label);

        repository.save(task);
    }

    @Test
    @Order(1)
    public void testIndexWithAuth() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks").with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    @Order(2)
    void testIndexWithFilter() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks")
                        .with(token)
                        .param("status", "draft")
                        .param("labelId", String.valueOf(label.getId())))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray().hasSize(1);
    }

    @Test
    @Order(3)
    public void testIndexNoAuth() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    public void testShow() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks/" + task.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(task.getName()),
                v -> v.node("status").isEqualTo(task.getTaskStatus().getSlug())
        );
    }

    @Test
    @Order(5)
    public void testShowNoAuth() throws Exception {
        mockMvc.perform(get("/api/tasks/" + task.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(6)
    void testCreate() throws Exception {
        TaskCreateDTO dto = new TaskCreateDTO();
        dto.setTitle("title");
        dto.setStatus("testStatus");
        dto.setAssigneeId(JsonNullable.of(user.getId()));

        var request = post("/api/tasks").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
    }

    @Test
    @Order(7)
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
    @Order(8)
    public void testUpdate() throws Exception {
        Map<String, String> data = Map.of("index", "100", "title", "newName");
        user.addTask(task);
        userRepository.save(user);

        var request = put("/api/tasks/" + task.getId()).with(token)
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
    @Order(9)
    public void testUpdateNoAuth() throws Exception {
        Map<String, String> data = Map.of("index", "100", "name", "newName");

        var request = put("/api/tasks/" + task.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    //TODO uncomment the code below when task ownership logic is required
//    @Test
//    @Order(10)
//    public void testUpdateForbidden() throws Exception {
//        Map<String, String> data = Map.of("index", "100", "name", "newName");
//
//        var request = put("/api/tasks/" + task.getId()).with(wrongToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(data));
//        mockMvc.perform(request)
//                .andExpect(status().isForbidden());
//    }

    @Test
    @Order(11)
    public void testDelete()
            throws Exception {
        user.addTask(task);
        userRepository.save(user);

        var request = delete("/api/tasks/" + task.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        Task opActual = repository.findById(task.getId()).orElse(null);

        assertThat(opActual).isNull();
    }

    @Test
    @Order(12)
    public void testDeleteNoAuth() throws Exception {
        var request = delete("/api/tasks/" + task.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
//TODO uncomment the code below when task ownership logic is required
//    @Test
//    @Order(13)
//    public void testDeleteForbidden() throws Exception {
//        var request = delete("/api/tasks/" + task.getId()).with(wrongToken);
//        mockMvc.perform(request)
//                .andExpect(status().isForbidden());
//    }
}
