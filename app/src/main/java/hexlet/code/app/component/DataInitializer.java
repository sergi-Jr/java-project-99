package hexlet.code.app.component;

import hexlet.code.app.user.UserCreateDTO;
import hexlet.code.app.user.UserMapper;
import hexlet.code.app.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
//@Profile("development") TODO разобраться как актвировать профайлы
public class DataInitializer implements ApplicationRunner {
    @Autowired
    private final UserRepository repository;

    @Autowired
    private final UserMapper mapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        UserCreateDTO data = new UserCreateDTO();
        data.setEmail("hexlet@example.com");
        data.setPassword("qwerty");
        repository.save(mapper.map(data));
    }
}
