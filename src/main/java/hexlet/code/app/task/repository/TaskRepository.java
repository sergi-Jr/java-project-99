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
            select * from tasks
            where tasks.assignee_id = ?1
            """, nativeQuery = true)
    List<Task> findAllByUserId(Long userId);
}
