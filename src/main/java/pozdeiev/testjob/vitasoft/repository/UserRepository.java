package pozdeiev.testjob.vitasoft.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pozdeiev.testjob.vitasoft.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLogin(String login);
}
