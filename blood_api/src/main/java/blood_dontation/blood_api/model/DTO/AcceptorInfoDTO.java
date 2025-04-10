package blood_dontation.blood_api.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcceptorInfoDTO {
    private String name;
    private String phoneNumber;
    private Point location;
    private String place;
}
