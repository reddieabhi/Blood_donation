package blood_dontation.blood_api.model.DTO;

import blood_dontation.blood_api.model.User;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.awt.*;
import java.util.UUID;


@Getter
@Setter
public class EventDetailsDTO {
    private UUID eventID;
    private String bloodGroup;
    private String userName;
    private String place;
    private Point location;

    public EventDetailsDTO(User user, UUID eid, String bloodGroup, org.locationtech.jts.geom.Point location, String place) {
        this.eventID = eid;
        this.bloodGroup = bloodGroup;
        this.location = location;
        this.place = place;
        this.userName = user.getUserName();
    }
}
