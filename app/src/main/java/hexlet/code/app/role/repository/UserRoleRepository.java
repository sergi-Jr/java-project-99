package hexlet.code.app.role.repository;

import hexlet.code.app.role.model.UserRole;
import hexlet.code.app.role.type.UserRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    @Query("""
            select ur.type from UserRole ur
            where ur.user.id = :userId
            """)
    Set<UserRoleType> findRoleTypesByUserId(Long userId);
}
