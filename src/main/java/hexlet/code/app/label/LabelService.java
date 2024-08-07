package hexlet.code.app.label;

import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.exception.UnableDeletionException;
import hexlet.code.app.label.dto.LabelCreateDTO;
import hexlet.code.app.label.dto.LabelDTO;
import hexlet.code.app.label.dto.LabelUpdateDTO;
import hexlet.code.app.label.mapper.LabelMapper;
import hexlet.code.app.task.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class LabelService {
    @Autowired
    private LabelRepository repository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelMapper mapper;

    public List<LabelDTO> getAll() {
        List<Label> labels = repository.findAll();
        return labels.stream().map(mapper::map).toList();
    }

    public LabelDTO getOneById(Long id) {
        Label label = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        return mapper.map(label);
    }

    @Transactional
    public LabelDTO add(LabelCreateDTO data) {
        Label label = mapper.map(data);
        repository.save(label);
        return mapper.map(label);
    }

    @Transactional
    public LabelDTO update(Long id, LabelUpdateDTO data) {
        Label label = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
        mapper.update(data, label);
        repository.save(label);
        return mapper.map(label);
    }

    @Transactional
    public void delete(Long id) {
        Set<Long> links = repository.getIdFromCrossTable(id);
        if (!links.isEmpty()) {
            throw new UnableDeletionException("Label has active tasks");
        }
        repository.deleteById(id);
    }
}
