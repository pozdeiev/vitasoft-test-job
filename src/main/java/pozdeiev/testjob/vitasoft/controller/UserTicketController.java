package pozdeiev.testjob.vitasoft.controller;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pozdeiev.testjob.vitasoft.model.*;
import pozdeiev.testjob.vitasoft.repository.TicketRepository;
import pozdeiev.testjob.vitasoft.repository.UserRepository;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static pozdeiev.testjob.vitasoft.model.Ticket.Status.DRAFT;
import static pozdeiev.testjob.vitasoft.model.Ticket.Status.SENT;

@RestController
@RequestMapping(UserTicketController.URI)
@PreAuthorize("hasRole('USER')")
@Transactional
@Validated
public class UserTicketController {

    public static final String URI = "/user/ticket";

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserTicketController(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    @ResponseBody
    public Response<TicketDto> create(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestBody @NotNull TicketDto ticketDto
    ) {
        val user = getUserBy(userDetails);
        val ticket = new Ticket();
        ticket.setAuthor(user);
        ticket.setStatus(DRAFT);
        ticket.setText(ticketDto.getText());
        ticketRepository.save(ticket);

        return CommonResponse.success(TicketDto.of(ticket));
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Response<TicketDto> get(@AuthenticationPrincipal UserDetails userDetails, @PathVariable @Min(1) Long id) {
        return CommonResponse.success(TicketDto.of(getTicketBy(userDetails, id)));
    }

    @GetMapping
    @ResponseBody
    public Response<List<TicketDto>> list(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestParam(defaultValue = "1") @Min(1) int page
    ) {
        val user = getUserBy(userDetails);
        val pageRequest = PageRequest.of(page - 1, 20);
        val tickets = ticketRepository.findByAuthor(user, pageRequest);

        return CommonResponse.success(tickets.stream().map(TicketDto::of).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    @ResponseBody
    public Response<TicketDto> update(
        @AuthenticationPrincipal UserDetails userDetails,
        @PathVariable @Min(1) Long id,
        @RequestBody @NotNull TicketDto ticketDto
    ) {
        val ticket = getTicketBy(userDetails, id);

        if (!ticket.getStatus().equals(DRAFT)) {
            throw new BadRequestException("Status is not " + DRAFT);
        }

        ticket.setText(ticketDto.getText());
        ticketRepository.save(ticket);

        return CommonResponse.success(TicketDto.of(ticket));
    }

    @PutMapping("/{id}/send")
    @ResponseBody
    public Response<TicketDto> send(@AuthenticationPrincipal UserDetails userDetails, @PathVariable @Min(1) Long id) {
        val ticket = getTicketBy(userDetails, id);

        if (!ticket.getStatus().equals(SENT)) {
            ticket.setStatus(SENT);
            ticketRepository.save(ticket);
        }

        return CommonResponse.success(TicketDto.of(ticket));
    }

    private Ticket getTicketBy(UserDetails userDetails, Long id) {
        val user = getUserBy(userDetails);
        val ticket = ticketRepository.findById(id)
            .orElseThrow(NotFoundException::new);

        if (!user.equals(ticket.getAuthor())) {
            throw new BadRequestException();
        }

        return ticket;
    }

    private User getUserBy(UserDetails userDetails) {
        return userRepository.findByLogin(userDetails.getUsername())
            .orElseThrow(NotFoundException::new);
    }
}
