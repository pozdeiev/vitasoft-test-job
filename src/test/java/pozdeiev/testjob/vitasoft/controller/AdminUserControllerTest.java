package pozdeiev.testjob.vitasoft.controller;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pozdeiev.testjob.vitasoft.model.User.Role.*;

@Transactional
class AdminUserControllerTest extends AbstractControllerTest {

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void test() throws Exception {
        mockMvc.perform(get(OperatorTicketController.URI))
            .andDo(print())
            .andExpect(status().isForbidden())
        ;

        mockMvc.perform(get(UserTicketController.URI))
            .andDo(print())
            .andExpect(status().isForbidden())
        ;

        val operator = userRepository.findByLogin("operator").orElseThrow(EntityNotFoundException::new);

        mockMvc.perform(get(AdminUserController.URI))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result", hasSize(3)))
            .andExpect(jsonPath("$.result[0].roles[0]").value(ADMIN.name()))
            .andExpect(jsonPath("$.result[1].roles[0]").value(OPERATOR.name()))
            .andExpect(jsonPath("$.result[2].roles[0]").value(USER.name()))
        ;

        mockMvc.perform(put(AdminUserController.URI + '/' + operator.getId() + "/revoke/operator"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.roles", hasSize(0)))
        ;

        mockMvc.perform(get(AdminUserController.URI))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result", hasSize(3)))
            .andExpect(jsonPath("$.result[0].roles[0]").value(ADMIN.name()))
            .andExpect(jsonPath("$.result[1].roles[0]").doesNotExist())
            .andExpect(jsonPath("$.result[2].roles[0]").value(USER.name()))
        ;

        mockMvc.perform(put(AdminUserController.URI + '/' + operator.getId() + "/grant/operator"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.roles[0]").value(OPERATOR.name()))
        ;

        mockMvc.perform(get(AdminUserController.URI))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result", hasSize(3)))
            .andExpect(jsonPath("$.result[0].roles[0]").value(ADMIN.name()))
            .andExpect(jsonPath("$.result[1].roles[0]").value(OPERATOR.name()))
            .andExpect(jsonPath("$.result[2].roles[0]").value(USER.name()))
        ;
    }
}