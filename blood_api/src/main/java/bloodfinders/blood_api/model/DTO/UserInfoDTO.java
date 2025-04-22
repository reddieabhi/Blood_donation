package bloodfinders.blood_api.model.DTO;

import bloodfinders.blood_api.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private String name;
    private String phoneNumber;
    private String place;
    private String city;
    private String bloodGroup;
    private double latitude;
    private double longitude;
    private String email;

    public UserInfoDTO(User user){
        this.name = user.getUserName();
        this.phoneNumber = user.getPhoneNumber();
        this.place = user.getPlace();
        this.city = user.getCity();
        this.bloodGroup = user.getBloodGroup();
        this.latitude = user.getLocation().getX();
        this.longitude = user.getLocation().getY();
        this.email = user.getEmail();
    }
}
