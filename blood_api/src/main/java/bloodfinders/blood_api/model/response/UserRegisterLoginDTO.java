package bloodfinders.blood_api.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class UserRegisterLoginDTO {
    private UUID userId;
    private String jwtToken;

    public UserRegisterLoginDTO(UUID uid, String jwtToken) {
        this.userId = uid;
        this.jwtToken = jwtToken;
    }
}
