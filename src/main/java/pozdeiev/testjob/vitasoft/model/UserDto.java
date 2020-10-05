package pozdeiev.testjob.vitasoft.model;

import lombok.Value;

import java.util.Set;

@Value
public class UserDto {

    Long id;
    String login;
    Set<User.Role> roles;

    public static UserDto of(User user) {
        return new UserDto(user.getId(), user.getLogin(), user.getRoles());
    }
}
