package hexlet.code.app.label.mapper;

import hexlet.code.app.label.Label;
import hexlet.code.app.label.dto.LabelCreateDTO;
import hexlet.code.app.label.dto.LabelDTO;
import hexlet.code.app.label.dto.LabelUpdateDTO;
import hexlet.code.app.mapper.JsonNullableMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = JsonNullableMapper.class,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class LabelMapper {
    public abstract LabelDTO map(Label model);

    public abstract Label map(LabelCreateDTO dto);

    public abstract void update(LabelUpdateDTO dto, @MappingTarget Label model);
}
