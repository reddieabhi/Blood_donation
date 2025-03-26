package blood_dontation.blood_api.model.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID eid;

    @ManyToOne
    @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false)
    private User user;

    private String bloodGroup;
    private String currentStatus;

    @JsonIgnore
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    // Getters and Setters
    public UUID getEid() {
        return eid;
    }

    public void setEid(UUID eid) {
        this.eid = eid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
}
