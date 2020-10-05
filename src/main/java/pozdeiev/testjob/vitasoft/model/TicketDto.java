package pozdeiev.testjob.vitasoft.model;

import lombok.Value;

@Value
public class TicketDto {

    Long id;
    Long authorId;
    Ticket.Status status;
    String text;

    public static TicketDto of(Ticket ticket) {
        return new TicketDto(ticket.getId(), ticket.getAuthor().getId(), ticket.getStatus(), ticket.getText());
    }
}
