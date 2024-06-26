package hexlet.code.app.user;

import hexlet.code.app.mapper.JsonNullableMapper;
import hexlet.code.app.mapper.ReferenceMapper;
import hexlet.code.app.user.dto.UserCreateDTO;
import hexlet.code.app.user.dto.UserDTO;
import hexlet.code.app.user.dto.UserUpdateDTO;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {
    @Autowired
    private PasswordEncoder encoder;

    @BeforeMapping
    public void encryptPassword(UserCreateDTO dto) {
        String password = dto.getPassword();
        dto.setPassword(encoder.encode(password));
    }

    public abstract User map(UserCreateDTO dto);

    public abstract UserDTO map(User model);

    public abstract UserCreateDTO mapToCreateDTO(User model);

    public abstract void update(UserUpdateDTO dto, @MappingTarget User model);
}
