package pozdeiev.testjob.vitasoft.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;

import javax.persistence.*;

@Data
@EqualsAndHashCode
@ToString
@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    private Status status;

    private String text;

    public static Ticket of(User author, Status status, String text) {
        val ticket = new Ticket();
        ticket.setAuthor(author);
        ticket.setStatus(status);
        ticket.setText(text);
        return ticket;
    }

    public enum Status {
        DRAFT,
        SENT,
        ACCEPTED,
        REFUSED
    }
}
