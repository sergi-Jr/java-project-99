package hexlet.code.app.label;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
    Optional<Label> findByName(String name);

    @Query(value = """
            select label_id from task_labels
            where label_id = ?1
            """, nativeQuery = true)
    Set<Long> getIdFromCrossTable(Long id);
}
