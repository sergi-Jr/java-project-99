package hexlet.code.app.user.service;

import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.exception.UnableDeletionException;
import hexlet.code.app.task.model.Task;
import hexlet.code.app.task.repository.TaskRepository;
import hexlet.code.app.user.User;
import hexlet.code.app.user.UserMapper;
import hexlet.code.app.user.UserRepository;
import hexlet.code.app.user.dto.UserCreateDTO;
import hexlet.code.app.user.dto.UserDTO;
import hexlet.code.app.user.dto.UserUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("UserService")
@Transactional(readOnly = true)
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserMapper mapper;

    public List<UserDTO> getAll() {
        List<User> users = repository.findAll();
        return users.stream().map(mapper::map).toList();
    }

    public UserDTO getOneById(long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"));
        return mapper.map(user);
    }

    @Transactional
    public UserDTO add(UserCreateDTO data) {
        User user = mapper.map(data);
        repository.save(user);
        return mapper.map(user);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO data) {
        User user = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        mapper.update(data, user);
        repository.save(user);
        return mapper.map(user);
    }

    @Transactional
    public void delete(Long id) {
        List<Task> tasks = taskRepository.findAllByUserId(id);

        if (!tasks.isEmpty()) {
            throw new UnableDeletionException("User has active tasks");
        }
        repository.deleteById(id);
    }

    public Long getIdByEmail(String email) {
        return repository.getReferenceByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Not found"))
                .getId();
    }
}
