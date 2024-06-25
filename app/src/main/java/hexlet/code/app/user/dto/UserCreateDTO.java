package hexlet.code.app.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserCreateDTO {
    @Email
    @NotBlank
    private String email;

    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;

    @NotBlank
    @Size(min = 3, max = 100)
    private String password;
}
