package pozdeiev.testjob.vitasoft.service;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pozdeiev.testjob.vitasoft.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

import static pozdeiev.testjob.vitasoft.model.User.Role.*;

@Service
@Transactional
public class UserDetailService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserDetailService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        seed();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val user = userRepository.findByLogin(username)
            .orElseThrow(() -> new UsernameNotFoundException("No user found by email " + username));
        val authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.name()))
            .collect(Collectors.toSet());

        return new User(user.getLogin(), user.getPassword(), authorities);
    }

    private void seed() {
        if (userRepository.count() != 0) {
            return;
        }

        String password = passwordEncoder.encode("password");

        userRepository.save(pozdeiev.testjob.vitasoft.model.User.of("admin", password, ADMIN));
        userRepository.save(pozdeiev.testjob.vitasoft.model.User.of("operator", password, OPERATOR));
        userRepository.save(pozdeiev.testjob.vitasoft.model.User.of("user", password, USER));
    }
}
