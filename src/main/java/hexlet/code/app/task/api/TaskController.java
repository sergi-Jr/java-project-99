package hexlet.code.app.task.api;

import hexlet.code.app.task.dto.TaskCreateDTO;
import hexlet.code.app.task.dto.TaskDTO;
import hexlet.code.app.task.dto.TaskParamsDTO;
import hexlet.code.app.task.dto.TaskUpdateDTO;
import hexlet.code.app.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@EnableMethodSecurity
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<TaskDTO>> index(TaskParamsDTO params, @RequestParam(defaultValue = "1") int page) {
        List<TaskDTO> resBody = taskService.getAll(params, page);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(resBody.size()))
                .body(resBody);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO show(@PathVariable Long id) {
        return taskService.getOneById(id);
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDTO create(@Valid @RequestBody TaskCreateDTO data) {
        return taskService.add(data);
    }

    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    //TODO uncomment the code below when task ownership logic is required
    //@PreAuthorize("@TaskService.getTaskUserId(#id) == @UserService.getIdByEmail(authentication.name)")
    public TaskDTO update(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO data) {
        return taskService.update(id, data);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //TODO uncomment the code below when task ownership logic is required
    //@PreAuthorize("@TaskService.getTaskUserId(#id) == @UserService.getIdByEmail(authentication.name)")
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
