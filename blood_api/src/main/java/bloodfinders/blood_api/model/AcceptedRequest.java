package bloodfinders.blood_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "accepted_requests", uniqueConstraints ={@UniqueConstraint(columnNames = {"eventId", "acceptorId"})} )
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AcceptedRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID eventId;
    private UUID acceptorId;
    private UUID requesterId;
}
