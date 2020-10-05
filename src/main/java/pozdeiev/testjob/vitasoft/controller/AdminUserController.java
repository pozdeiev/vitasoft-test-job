package pozdeiev.testjob.vitasoft.controller;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pozdeiev.testjob.vitasoft.model.CommonResponse;
import pozdeiev.testjob.vitasoft.model.Response;
import pozdeiev.testjob.vitasoft.model.UserDto;
import pozdeiev.testjob.vitasoft.repository.UserRepository;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

import static pozdeiev.testjob.vitasoft.model.User.Role.OPERATOR;

@RestController
@RequestMapping(AdminUserController.URI)
@PreAuthorize("hasRole('ADMIN')")
@Transactional
@Validated
public class AdminUserController {

    public static final String URI = "/admin/user";

    private final UserRepository userRepository;

    @Autowired
    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    @ResponseBody
    public Response<List<UserDto>> list(@RequestParam(defaultValue = "1") @Min(1) int page) {
        val pageRequest = PageRequest.of(page - 1, 20);
        val users = userRepository.findAll(pageRequest);

        return CommonResponse.success(users.stream().map(UserDto::of).collect(Collectors.toList()));
    }

    @PutMapping("/{id}/grant/operator")
    @ResponseBody
    public Response<UserDto> grantOperator(@PathVariable @Min(1) Long id) {
        val user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        user.getRoles().add(OPERATOR);
        userRepository.save(user);

        return CommonResponse.success(UserDto.of(user));
    }

    @PutMapping("/{id}/revoke/operator")
    @ResponseBody
    public Response<UserDto> revokeOperator(@PathVariable @Min(1) Long id) {
        val user = userRepository.findById(id).orElseThrow(NotFoundException::new);
        user.getRoles().remove(OPERATOR);
        userRepository.save(user);

        return CommonResponse.success(UserDto.of(user));
    }
}
