package hexlet.code.app.user.api;

import hexlet.code.app.user.dto.UserCreateDTO;
import hexlet.code.app.user.dto.UserDTO;
import hexlet.code.app.user.service.UserService;
import hexlet.code.app.user.dto.UserUpdateDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@EnableMethodSecurity
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@RoleService.hasAnyRoleByUserId(@UserRole.user)")
    public List<UserDTO> index() {
        return userService.getAll();
    }

    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@RoleService.hasAnyRoleByUserId(@UserRole.user)")
    public UserDTO show(@PathVariable Long id) {
        return userService.getOneById(id);
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@RoleService.hasAnyRoleByUserId(@UserRole.admin)")
    public UserDTO create(@Valid @RequestBody UserCreateDTO data) {
        return userService.add(data);
    }

    @PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@RoleService.isTrueUserOwnerByUserId(#id) AND"
            + "@RoleService.hasAnyRoleByUserId(@UserRole.owner)")
    public UserDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO data) {
        return userService.update(id, data);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@RoleService.isTrueUserOwnerByUserId(#id) AND"
            + "@RoleService.hasAnyRoleByUserId(@UserRole.owner)")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
