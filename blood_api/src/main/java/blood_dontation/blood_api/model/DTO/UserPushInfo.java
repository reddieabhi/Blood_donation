package blood_dontation.blood_api.model.DTO;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;


@Getter
@Setter
public class UserPushInfo {
    private UUID userId;
    private String pushToken;
}
