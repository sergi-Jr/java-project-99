package hexlet.code.app.task.api;

import hexlet.code.app.task.dto.TaskStatusCreateDTO;
import hexlet.code.app.task.dto.TaskStatusDTO;
import hexlet.code.app.task.dto.TaskStatusUpdateDTO;
import hexlet.code.app.task.service.TaskStatusService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@EnableMethodSecurity
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    @Autowired
    private TaskStatusService statusService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaskStatusDTO>> index() {
        List<TaskStatusDTO> resBody =  statusService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(resBody.size()))
                .body(resBody);
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO show(@PathVariable Long id) {
        return statusService.getOneById(id);
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO data) {
        return statusService.add(data);
    }

    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public TaskStatusDTO update(@Valid @RequestBody TaskStatusUpdateDTO data, @PathVariable Long id) {
        return statusService.update(id, data);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        statusService.delete(id);
    }
}
