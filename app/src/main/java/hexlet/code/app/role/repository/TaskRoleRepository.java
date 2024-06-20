package hexlet.code.app.role.repository;

import hexlet.code.app.role.model.TaskRole;
import hexlet.code.app.role.type.TaskRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TaskRoleRepository extends JpaRepository<TaskRole, Long> {
    Set<TaskRoleType> findRoleTypesByUserIdAndTaskId(Long userId, Long taskId);
}

