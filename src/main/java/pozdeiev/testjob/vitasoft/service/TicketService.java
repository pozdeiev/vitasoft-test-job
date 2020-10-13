package pozdeiev.testjob.vitasoft.service;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pozdeiev.testjob.vitasoft.controller.NotFoundException;
import pozdeiev.testjob.vitasoft.model.Ticket;
import pozdeiev.testjob.vitasoft.model.User;
import pozdeiev.testjob.vitasoft.repository.TicketRepository;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    @Autowired
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> findByAuthor(User user, int page) {
        val pageRequest = PageRequest.of(page - 1, 20);
        return ticketRepository.findByAuthor(user, pageRequest);
    }

    public Ticket findById(Long id) {
        return ticketRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public List<Ticket> findByStatus(Ticket.Status status, int page) {
        val pageRequest = PageRequest.of(page - 1, 20);
        return ticketRepository.findByStatus(status, pageRequest);
    }

    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
}
