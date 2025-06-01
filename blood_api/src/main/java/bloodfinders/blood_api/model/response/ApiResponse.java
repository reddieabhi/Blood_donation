package bloodfinders.blood_api.model.response;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ApiResponse {
    private String message;
    private  int statusCode;
    private String payload;
    private String jwtToken;
    private UUID uuid;
}
