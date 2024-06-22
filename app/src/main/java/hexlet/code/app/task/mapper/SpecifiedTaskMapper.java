package hexlet.code.app.task.mapper;

import hexlet.code.app.task.model.TaskStatus;
import hexlet.code.app.task.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class SpecifiedTaskMapper {
    @Autowired
    private TaskStatusRepository statusRepository;

    public TaskStatus toTaskStatus(String slug) {
        return slug != null
                ? statusRepository.findBySlug(slug).orElse(null)
                : null;
    }
}
