package bloodfinders.blood_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uid;

    private String userName;
    private String password;
    private String name;
    private String place;
    private String DOB;
    private String email;
    private String city;
    private int weight;
    private String phoneNumber;
    private String bloodGroup;

    @JsonIgnore
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    private String pushToken;


}