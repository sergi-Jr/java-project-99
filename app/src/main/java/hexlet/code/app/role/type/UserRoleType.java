package hexlet.code.app.role.type;

import hexlet.code.app.role.Role;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum UserRoleType implements Role {
    ADMIN, USER, OWNER;

    @Component("UserRole")
    @Getter
    static class SpringComponent {
        private final UserRoleType admin = UserRoleType.ADMIN;
        private final UserRoleType user = UserRoleType.USER;
        private final UserRoleType owner = UserRoleType.OWNER;
    }

    private final Set<Role> children = new HashSet<>();

    static {
        ADMIN.children.add(OWNER);
        OWNER.children.addAll(List.of(USER, TaskRoleType.EDITOR));
        USER.children.add(TaskRoleType.VIEWER);
    }

    @Override
    public boolean includes(Role role) {
        return this.equals(role) || children.stream().anyMatch(r -> r.includes(role));
    }
}

