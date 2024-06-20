package hexlet.code.app.role.type;

import hexlet.code.app.role.Role;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

public enum TaskRoleType implements Role {
    VIEWER, EDITOR;

    @Component("TaskRole")
    @Getter
    static class SpringComponent {
        private final TaskRoleType editor = TaskRoleType.EDITOR;
        private final TaskRoleType viewer = TaskRoleType.VIEWER;
    }

    private final Set<Role> children = new HashSet<>();

    static {
        EDITOR.children.add(VIEWER);
    }

    @Override
    public boolean includes(Role role) {
        return this.equals(role) || children.stream().anyMatch(r -> r.includes(role));
    }
}
