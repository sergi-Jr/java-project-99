package hexlet.code.app.task.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.app.label.Label;
import hexlet.code.app.label.LabelRepository;
import hexlet.code.app.task.model.TaskStatus;
import hexlet.code.app.task.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.Set;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class SpecifiedTaskMapper {
    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public TaskStatus toTaskStatus(String slug) {
        return slug != null
                ? statusRepository.findBySlug(slug).orElse(null)
                : null;
    }

    public Set<Label> toLabel(String name) {
        if (name != null) {
            Optional<Label> label = labelRepository.findByName(name);
            if (label.isPresent()) {
                return Set.of(label.get());
            }
        }
        return null;
    }

    public String fromLabels(Set<Label> labels) throws Exception {
        return objectMapper.writeValueAsString(labels);
    }
}
