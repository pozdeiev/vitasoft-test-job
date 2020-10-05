package pozdeiev.testjob.vitasoft.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pozdeiev.testjob.vitasoft.model.Ticket;
import pozdeiev.testjob.vitasoft.model.User;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(Ticket.Status status, Pageable pageable);
    List<Ticket> findByAuthor(User user, Pageable pageable);
}
