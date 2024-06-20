package hexlet.code.app.role.service;

import hexlet.code.app.role.Role;
import hexlet.code.app.role.repository.TaskRoleRepository;
import hexlet.code.app.role.repository.UserRoleRepository;
import hexlet.code.app.role.type.TaskRoleType;
import hexlet.code.app.role.type.UserRoleType;
import hexlet.code.app.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service("RoleService")
@Transactional(readOnly = true)
public class RoleService {
    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private TaskRoleRepository taskRoleRepository;

    public boolean hasAnyRoleByUserId(Role... roles) {
        long userId = ((User) getCurrentAuth().getPrincipal()).getId();
        Set<UserRoleType> roleTypes = userRoleRepository.findRoleTypesByUserId(userId);
        for (Role role : roles) {
            if (roleTypes.stream().anyMatch(rt -> rt.includes(role))) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyRoleByTaskId(Long taskId, Role... roles) {
        long userId = ((User) getCurrentAuth().getPrincipal()).getId();
        Set<UserRoleType> roleTypes = userRoleRepository.findRoleTypesByUserId(userId);
        for (Role role : roles) {
            if (roleTypes.stream().anyMatch(rt -> rt.includes(role))) {
                return true;
            }
        }

        Set<TaskRoleType> taskRoleTypes = taskRoleRepository.findRoleTypesByUserIdAndTaskId(userId, taskId);
        for (Role role : roles) {
            if (taskRoleTypes.stream().anyMatch(rt -> rt.includes(role))) {
                return true;
            }
        }

        return false;
    }

    public boolean isTrueUserOwnerByUserId(Long id) {
        long userId = ((User) getCurrentAuth().getPrincipal()).getId();
        return userId == id;
    }

//TODO create isTrueOwner by taskId method

    private static Authentication getCurrentAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
