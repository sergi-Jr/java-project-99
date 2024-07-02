package hexlet.code.app.task.repository;

import hexlet.code.app.task.model.Task;
import hexlet.code.app.task.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    List<Task> findAllByTaskStatus(TaskStatus status);

    @Query(value = """
            select * from Tasks t
            inner join TASK_LABELS tl on
            tl.label_id = ?1
            where tl.task_id = t.id
            """, nativeQuery = true)
    List<Task> findAllByLabel(Long labelId);
}
