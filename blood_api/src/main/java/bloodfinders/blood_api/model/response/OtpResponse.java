package bloodfinders.blood_api.model.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpResponse {
    private String message;
    private  int statusCode;
    private String payload;


}
