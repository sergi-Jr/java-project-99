package hexlet.code.app.task.mapper;

import hexlet.code.app.mapper.JsonNullableMapper;
import hexlet.code.app.task.dto.TaskStatusCreateDTO;
import hexlet.code.app.task.dto.TaskStatusDTO;
import hexlet.code.app.task.dto.TaskStatusUpdateDTO;
import hexlet.code.app.task.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskStatusMapper {
    public abstract TaskStatus map(TaskStatusCreateDTO dto);

    public abstract TaskStatusDTO map(TaskStatus model);

    public abstract void update(TaskStatusUpdateDTO dto, @MappingTarget TaskStatus model);
}
