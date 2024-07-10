package hexlet.code.app.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.label.Label;
import hexlet.code.app.label.mapper.LabelMapper;
import hexlet.code.app.label.LabelRepository;
import hexlet.code.app.label.dto.LabelCreateDTO;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LabelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Faker faker;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository statusRepository;

    private User user;

    private Label label;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    void setup() {
        user = userRepository.findByEmail("hexlet@example.com").get();
        token = jwt().jwt(b -> b.subject(user.getEmail()));
        label = new Label();
        label.setName(faker.internet().username());
        labelRepository.save(label);
    }

    @Test
    @Order(1)
    public void testIndexWithAuth() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/labels").with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    @Order(2)
    public void testIndexNoAuth() throws Exception {
        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(3)
    public void testShow() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/labels/" + label.getId()).with(token))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(label.getName())
        );
    }

    @Test
    @Order(4)
    public void testShowNoAuth() throws Exception {
        mockMvc.perform(get("/api/labels/" + user.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(5)
    void testCreate() throws Exception {
        var l = new LabelCreateDTO();
        l.setName("testName");

        var request = post("/api/labels").with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(labelMapper.map(l)));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        Optional<Label> opActual = labelRepository.findByName(l.getName());

        assertThat(opActual).isNotNull();
        Label actual = opActual.get();
        assertThat(l.getName()).isEqualTo(actual.getName());
    }

    @Test
    @Order(6)
    void testCreateNoAuth() throws Exception {
        var l = new LabelCreateDTO();
        l.setName("testName");

        var request = post("/api/labels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(l));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    public void testUpdate() throws Exception {
        Map<String, String> data = Map.of("name", "new");

        var request = put("/api/labels/" + label.getId()).with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        Optional<Label> opActual = labelRepository.findById(label.getId());

        assertThat(opActual).isNotNull();
        Label actual = opActual.get();
        assertThat(data).containsEntry("name", actual.getName());
    }

    @Test
    @Order(8)
    public void testUpdateNoAuth() throws Exception {
        Map<String, String> data = Map.of("name", "new");

        var request = put("/api/labels/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(9)
    public void testDelete() throws Exception {
        var request = delete("/api/labels/" + label.getId()).with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        Label opActual = labelRepository.findById(label.getId()).orElse(null);

        assertThat(opActual).isNull();
    }

    @Test
    @Order(10)
    public void testDeleteNoAuth() throws Exception {
        var request = delete("/api/labels/" + label.getId());
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
