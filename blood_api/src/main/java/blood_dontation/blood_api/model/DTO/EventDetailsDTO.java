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
    private double latitude;
    private double longitude;
    private UUID userId;
    private String currentStatus;

    public EventDetailsDTO(User user, UUID eid, String bloodGroup, Double latitude, Double longitude, String place, String currentStatus) {
        this.eventID = eid;
        this.bloodGroup = bloodGroup;
        this.latitude = latitude;
        this.longitude = longitude;
        this.place = place;
        this.userName = user.getUserName();
        this.userId = user.getUid();
        this.currentStatus = currentStatus;
    }
}
