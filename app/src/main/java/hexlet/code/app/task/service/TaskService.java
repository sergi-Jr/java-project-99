package hexlet.code.app.task.service;

import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.task.dto.TaskCreateDTO;
import hexlet.code.app.task.dto.TaskDTO;
import hexlet.code.app.task.dto.TaskParamsDTO;
import hexlet.code.app.task.dto.TaskUpdateDTO;
import hexlet.code.app.task.mapper.TaskMapper;
import hexlet.code.app.task.model.Task;
import hexlet.code.app.task.repository.TaskRepository;
import hexlet.code.app.task.specification.TaskSpecification;
import hexlet.code.app.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("TaskService")
@Transactional(readOnly = true)
public class TaskService {
    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskMapper mapper;

    @Autowired
    private TaskSpecification specification;

    public List<TaskDTO> getAll(TaskParamsDTO params, int page) {
        Specification<Task> spec = specification.build(params);
        Page<Task> tasks = repository.findAll(spec, PageRequest.of(page - 1, 10));
        return tasks.stream().map(mapper::map).toList();
    }

    public TaskDTO getOneById(long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        return mapper.map(task);
    }

    @Transactional
    public TaskDTO add(TaskCreateDTO data) {
        Task task = mapper.map(data);
        repository.save(task);
        return mapper.map(task);
    }

    @Transactional
    public TaskDTO update(Long id, TaskUpdateDTO data) {
        Task task = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        mapper.update(data, task);
        repository.save(task);
        return mapper.map(task);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Long getTaskUserId(Long id) {
        User owner = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"))
                .getAssignee();
        return owner == null ? null : owner.getId();
    }
}
