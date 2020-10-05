package pozdeiev.testjob.vitasoft.model;

import lombok.Value;

import java.util.stream.Collectors;

@Value
public class TicketDto {

    Long id;
    Long authorId;
    Ticket.Status status;
    String text;

    public static TicketDto of(Ticket ticket) {
        return new TicketDto(ticket.getId(), ticket.getAuthor().getId(), ticket.getStatus(), ticket.getText());
    }

    public TicketDto withHyphenizedText() {
        return new TicketDto(id, authorId, status, hyphenize(text));
    }

    public String hyphenize(String text) {
        return text.chars()
            .mapToObj(i -> String.valueOf((char)i))
            .collect(Collectors.joining("-"));
    }
}
