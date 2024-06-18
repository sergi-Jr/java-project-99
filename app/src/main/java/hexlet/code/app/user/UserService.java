package hexlet.code.app.user;

import hexlet.code.app.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    @Autowired
    private UserRepository repository;

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
        repository.deleteById(id);
    }
}
