package pozdeiev.testjob.vitasoft.controller;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import pozdeiev.testjob.vitasoft.model.Ticket;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pozdeiev.testjob.vitasoft.model.Ticket.Status.*;

@Transactional
class OperatorTicketControllerTest extends AbstractControllerTest {

    @Test
    @WithMockUser(username = "operator", roles = "OPERATOR")
    void test() throws Exception {
        mockMvc.perform(get(AdminUserController.URI))
            .andDo(print())
            .andExpect(status().isForbidden())
        ;

        mockMvc.perform(get(UserTicketController.URI))
            .andDo(print())
            .andExpect(status().isForbidden())
        ;

        val user = userRepository.findByLogin("user").orElseThrow(EntityNotFoundException::new);

        val ticketADraft = ticketRepository.save(Ticket.of(user, DRAFT, "Ticket A"));
        val ticketBSent = ticketRepository.save(Ticket.of(user, SENT, "Ticket B"));
        val ticketCSent = ticketRepository.save(Ticket.of(user, SENT, "Ticket C"));

        mockMvc.perform(get(OperatorTicketController.URI))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result", hasSize(2)))
            .andExpect(jsonPath("$.result[0].status").value(SENT.name()))
        ;

        mockMvc.perform(put(OperatorTicketController.URI + '/' + ticketADraft.getId() + "/accept"))
            .andDo(print())
            .andExpect(status().isBadRequest())
        ;

        mockMvc.perform(put(OperatorTicketController.URI + '/' + ticketBSent.getId() + "/accept"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.status").value(ACCEPTED.name()))
        ;

        mockMvc.perform(put(OperatorTicketController.URI + '/' + ticketBSent.getId() + "/refuse"))
            .andDo(print())
            .andExpect(status().isBadRequest())
        ;

        mockMvc.perform(put(OperatorTicketController.URI + '/' + ticketCSent.getId() + "/refuse"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result.status").value(REFUSED.name()))
        ;

        mockMvc.perform(put(OperatorTicketController.URI + '/' + ticketCSent.getId() + "/accept"))
            .andDo(print())
            .andExpect(status().isBadRequest())
        ;

        mockMvc.perform(get(OperatorTicketController.URI))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.result", hasSize(0)))
        ;
    }
}
