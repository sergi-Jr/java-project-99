package hexlet.code.app.task.service;

import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.exception.UnableDeletionException;
import hexlet.code.app.task.dto.TaskStatusCreateDTO;
import hexlet.code.app.task.dto.TaskStatusDTO;
import hexlet.code.app.task.dto.TaskStatusUpdateDTO;
import hexlet.code.app.task.mapper.TaskStatusMapper;
import hexlet.code.app.task.model.TaskStatus;
import hexlet.code.app.task.repository.TaskRepository;
import hexlet.code.app.task.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskStatusService {
    @Autowired
    private TaskStatusRepository repository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusMapper mapper;

    public List<TaskStatusDTO> getAll() {
        List<TaskStatus> statuses = repository.findAll();
        return statuses.stream().map(mapper::map).toList();
    }

    public TaskStatusDTO getOneById(long id) {
        TaskStatus status = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        return mapper.map(status);
    }

    @Transactional
    public TaskStatusDTO add(TaskStatusCreateDTO data) {
        TaskStatus status = mapper.map(data);
        repository.save(status);
        return mapper.map(status);
    }

    @Transactional
    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO data) {
        TaskStatus status = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        mapper.update(data, status);
        repository.save(status);
        return mapper.map(status);
    }

    @Transactional
    public void delete(Long id) {
        TaskStatus status = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        if (!taskRepository.findAllByTaskStatus(status).isEmpty()) {
            throw new UnableDeletionException("Status has active tasks");
        }
        repository.deleteById(id);
    }
}
