package blood_dontation.blood_api.model.DTO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uid;

    private String userName;
    private String password;
    private String name;
    private String place;
    private String DOB;
    private String email;
    private String city;
    private int weight;
}
