package bloodfinders.blood_api.model.DTO;

import bloodfinders.blood_api.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
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

    @Override
    public String toString() {
        return "EventDetailsDTO{" +
                "eventID=" + eventID +
                ", bloodGroup='" + bloodGroup + '\'' +
                ", userName='" + userName + '\'' +
                ", place='" + place + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", userId=" + userId +
                ", currentStatus='" + currentStatus + '\'' +
                '}';
    }

}
