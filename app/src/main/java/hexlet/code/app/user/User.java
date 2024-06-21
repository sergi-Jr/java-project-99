package hexlet.code.app.user;

import hexlet.code.app.model.BaseEntity;
import hexlet.code.app.role.model.TaskRole;
import hexlet.code.app.role.model.UserRole;
import hexlet.code.app.role.type.TaskRoleType;
import hexlet.code.app.role.type.UserRoleType;
import hexlet.code.app.task.model.Task;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@Getter
@Setter
public class User implements BaseEntity, UserDetails {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    private String firstName;

    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 3)
    private String password;

    @CreatedDate
    private LocalDate createdAt;

    @LastModifiedDate
    private LocalDate updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<TaskRole> taskRoles = new ArrayList<>();

    public void addUserRole(UserRoleType roleType) {
        final UserRole role = new UserRole();
        role.setType(roleType);
        role.setUser(this);
        userRoles.add(role);
    }

    public void removeUserRole(UserRoleType roleType) {
        userRoles.removeIf(ur -> ur.getType() == roleType);
    }

    public void addTaskRole(Task task, TaskRoleType roleType) {
        final TaskRole role = new TaskRole();
        role.setType(roleType);
        role.setUser(this);
        role.setTask(task);
        taskRoles.add(role);
    }

    //TODO create remove task role method

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptySet();
    }

    @Override
    public String getUsername() {
        return email;
    }
}
