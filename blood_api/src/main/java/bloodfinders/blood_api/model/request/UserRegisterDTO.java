package bloodfinders.blood_api.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {
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
    private double latitude;
    private double longitude;
    private String pushToken;


}
