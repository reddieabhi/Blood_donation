package bloodfinders.blood_api.model.DTO;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;


@Getter
@Setter
public class UserPushTokenDTO {
    private UUID userId;
    private String pushToken;

    public UserPushTokenDTO(UUID userId, String pushToken) {
        this.userId = userId;
        this.pushToken = pushToken;
    }
}

