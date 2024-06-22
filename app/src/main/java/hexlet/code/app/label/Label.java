package hexlet.code.app.label;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hexlet.code.app.serializer.LabelSerializer;
import hexlet.code.app.task.model.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "labels")
@ToString(onlyExplicitlyIncluded = true)
@Getter
@Setter
@JsonSerialize(using = LabelSerializer.class)
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, columnDefinition = "TEXT")
    @ToString.Include
    private String name;

    @ManyToMany(mappedBy = "labels", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();

    @CreatedDate
    private LocalDate createdAt;
}
