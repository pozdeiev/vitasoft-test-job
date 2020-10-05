package pozdeiev.testjob.vitasoft.controller;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pozdeiev.testjob.vitasoft.model.CommonResponse;
import pozdeiev.testjob.vitasoft.model.Response;
import pozdeiev.testjob.vitasoft.model.Ticket;
import pozdeiev.testjob.vitasoft.model.TicketDto;
import pozdeiev.testjob.vitasoft.repository.TicketRepository;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(OperatorTicketController.URI)
@PreAuthorize("hasRole('OPERATOR')")
@Transactional
@Validated
public class OperatorTicketController {

    public static final String URI = "/operator/ticket";

    private final TicketRepository ticketRepository;

    @Autowired
    public OperatorTicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    @ResponseBody
    public Response<List<TicketDto>> list(@RequestParam(defaultValue = "1") @Min(1) int page) {
        val pageRequest = PageRequest.of(page - 1, 20);
        val tickets = ticketRepository.findByStatus(Ticket.Status.SENT, pageRequest);

        return CommonResponse.success(tickets.stream()
            .map(ticket -> TicketDto.of(ticket).withHyphenizedText())
            .collect(Collectors.toList()));
    }

    @PutMapping("/{id}/accept")
    @ResponseBody
    public Response<TicketDto> accept(@PathVariable @Min(1) Long id) {
        val ticket = updateTicketStatus(id, Ticket.Status.ACCEPTED);

        return CommonResponse.success(TicketDto.of(ticket));
    }

    @PutMapping("/{id}/refuse")
    @ResponseBody
    public Response<TicketDto> refuse(@PathVariable @Min(1) Long id) {
        val ticket = updateTicketStatus(id, Ticket.Status.REFUSED);

        return CommonResponse.success(TicketDto.of(ticket));
    }

    private Ticket updateTicketStatus(Long id, Ticket.Status status) {
        val ticket = ticketRepository.findById(id)
            .orElseThrow(NotFoundException::new);

        if (!ticket.getStatus().equals(Ticket.Status.SENT)) {
            throw new BadRequestException("Status is not " + Ticket.Status.SENT);
        }

        ticket.setStatus(status);
        return ticketRepository.save(ticket);
    }
}
