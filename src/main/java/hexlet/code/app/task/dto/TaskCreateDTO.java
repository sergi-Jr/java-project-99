package hexlet.code.app.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskCreateDTO {
    @NotBlank
    @Size(min = 1)
    private String title;

    private JsonNullable<Integer> index;

    private JsonNullable<String> content;

    private String status;

    private JsonNullable<Set<Long>> taskLabelIds;

    private JsonNullable<Long> assigneeId;
}
