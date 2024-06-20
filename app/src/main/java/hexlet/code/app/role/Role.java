package hexlet.code.app.role;

import hexlet.code.app.role.type.UserRoleType;

import java.util.Set;

public interface Role {
    boolean includes(Role role);

    static Set<Role> roots() {
        return Set.of(UserRoleType.ADMIN);
    }
}
