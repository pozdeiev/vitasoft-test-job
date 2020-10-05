package pozdeiev.testjob.vitasoft.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import pozdeiev.testjob.vitasoft.model.CommonResponse;
import pozdeiev.testjob.vitasoft.model.TicketDto;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static pozdeiev.testjob.vitasoft.model.Ticket.Status.DRAFT;
import static pozdeiev.testjob.vitasoft.model.Ticket.Status.SENT;

@Transactional
class UserTicketControllerTest extends AbstractControllerTest {

    @Test
    @WithMockUser
    void test() throws Exception {
        mockMvc.perform(get(AdminUserController.URI))
            .andDo(print())
            .andExpect(status().isForbidden())
        ;

        mockMvc.perform(get(OperatorTicketController.URI))
            .andDo(print())
            .andExpect(status().isForbidden())
        ;

        mockMvc.perform(get(UserTicketController.URI + "?page=0"))
            .andDo(print())
            .andExpect(status().isBadRequest())
        ;

        val user = userRepository.findByLogin("user").orElseThrow(EntityNotFoundException::new);

        val ticketDtoA = new TicketDto(null, null, null, "Ticket A");
        val ticketDtoAJson = objectMapper.writeValueAsString(ticketDtoA);
        var requestBuilder = post(UserTicketController.URI)
            .content(ticketDtoAJson)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.result.text").value(ticketDtoA.getText()))
            .andExpect(jsonPath("$.result.authorId").value(user.getId()))
            .andExpect(jsonPath("$.result.status").value(DRAFT.name()))
            .andDo(mvcResult -> {
                val response = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsByteArray(),
                    new TypeReference<CommonResponse<TicketDto>>() {}
                );
                assertNotNull(response.getResult().getId());
                assertEquals(ticketDtoA.getText(), response.getResult().getText());

                mockMvc.perform(get(UserTicketController.URI + '/' + response.getResult().getId()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.result.text").value(ticketDtoA.getText()))
                    .andExpect(jsonPath("$.result.authorId").value(user.getId()))
                    .andExpect(jsonPath("$.result.status").value(DRAFT.name()))
                ;
            })
        ;

        val ticketDtoB = new TicketDto(null, null, null, "Ticket B");
        val ticketDtoBJson = objectMapper.writeValueAsString(ticketDtoB);
        requestBuilder = post(UserTicketController.URI)
            .content(ticketDtoBJson)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.result.text").value(ticketDtoB.getText()))
            .andExpect(jsonPath("$.result.authorId").value(user.getId()))
            .andExpect(jsonPath("$.result.status").value(DRAFT.name()))
            .andDo(mvcResult -> {
                val response = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsByteArray(),
                    new TypeReference<CommonResponse<TicketDto>>() {}
                );
                assertNotNull(response.getResult().getId());
                assertEquals(ticketDtoB.getText(), response.getResult().getText());

                val ticketDtoBUpdate = new TicketDto(null, null, null, ticketDtoB.getText() + " updated");
                val ticketDtoBUpdateJson = objectMapper.writeValueAsString(ticketDtoBUpdate);
                val ticketDtoBUpdateJsonRequestBuilder =
                    put(UserTicketController.URI + '/' + response.getResult().getId())
                        .content(ticketDtoBUpdateJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE);

                mockMvc.perform(ticketDtoBUpdateJsonRequestBuilder)
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.result.text").value(ticketDtoBUpdate.getText()))
                    .andExpect(jsonPath("$.result.authorId").value(user.getId()))
                    .andExpect(jsonPath("$.result.status").value(DRAFT.name()))
                ;

                mockMvc.perform(put(UserTicketController.URI + '/' + response.getResult().getId() + "/send"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.result.text").value(ticketDtoBUpdate.getText()))
                    .andExpect(jsonPath("$.result.authorId").value(user.getId()))
                    .andExpect(jsonPath("$.result.status").value(SENT.name()))
                ;
            })
        ;

        mockMvc.perform(get(UserTicketController.URI))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.result", hasSize(2)))
            .andExpect(jsonPath("$.result[0].text").value(ticketDtoA.getText()))
            .andExpect(jsonPath("$.result[0].authorId").value(user.getId()))
            .andExpect(jsonPath("$.result[0].status").value(DRAFT.name()))
            .andExpect(jsonPath("$.result[1].text").value(ticketDtoB.getText() + " updated"))
            .andExpect(jsonPath("$.result[1].authorId").value(user.getId()))
            .andExpect(jsonPath("$.result[1].status").value(SENT.name()))
            .andDo(mvcResult -> {
                val response = objectMapper.readValue(
                    mvcResult.getResponse().getContentAsByteArray(),
                    new TypeReference<CommonResponse<List<TicketDto>>>() {}
                );
                assertNotNull(response.getResult().get(0).getId());
                assertNotNull(response.getResult().get(1).getId());

                val ticketDtoBUpdate = new TicketDto(null, null, null, ticketDtoB.getText() + " denied update");
                val ticketDtoBUpdateJson = objectMapper.writeValueAsString(ticketDtoBUpdate);
                val ticketDtoBUpdateJsonRequestBuilder =
                    put(UserTicketController.URI + '/' + response.getResult().get(1).getId())
                        .content(ticketDtoBUpdateJson)
                        .contentType(MediaType.APPLICATION_JSON_VALUE);

                mockMvc.perform(ticketDtoBUpdateJsonRequestBuilder)
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                ;
            })
        ;
    }
}
