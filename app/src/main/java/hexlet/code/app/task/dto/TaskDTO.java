package hexlet.code.app.task.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class TaskDTO {
    private long id;
    private int index;
    private LocalDate createdAt;
    private String title;
    private long assigneeId;
    private String content;
    private String status;
    private Set<Long> taskLabelIds;
}
