package hexlet.code.app.label.mapper;

import hexlet.code.app.label.Label;
import hexlet.code.app.label.LabelRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public abstract class SpecifiedLabelMapper {
    @Autowired
    private LabelRepository labelRepository;

    public Set<Label> toLabelSet(Set<Long> ids) {
        return new HashSet<>(labelRepository.findAllById(ids));
    }

    public Set<Long> toLabelNames(Set<Label> labels) {
        return labels.stream().map(Label::getId).collect(Collectors.toSet());
    }
}
