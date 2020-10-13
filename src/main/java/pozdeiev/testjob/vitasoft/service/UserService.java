package pozdeiev.testjob.vitasoft.service;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pozdeiev.testjob.vitasoft.controller.NotFoundException;
import pozdeiev.testjob.vitasoft.model.User;
import pozdeiev.testjob.vitasoft.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> findAll(int page) {
        val pageRequest = PageRequest.of(page - 1, 20);
        return userRepository.findAll(pageRequest);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}
