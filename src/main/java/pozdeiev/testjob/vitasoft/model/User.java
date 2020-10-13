package pozdeiev.testjob.vitasoft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "login")
@ToString(of = {"id", "login", "roles"})
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true)
    private String login;

    private String password;

    @ElementCollection
    private Set<Role> roles = new HashSet<>();

    public static User of(Long id) {
        val user = new User();
        user.id = id;

        return user;
    }

    public static User of(String login, String password, Role ...roles) {
        val user = new User();
        user.login = login;
        user.password = password;
        user.roles.addAll(Arrays.asList(roles));

        return user;
    }

    public enum Role {
        ADMIN,
        OPERATOR,
        USER
    }
}
