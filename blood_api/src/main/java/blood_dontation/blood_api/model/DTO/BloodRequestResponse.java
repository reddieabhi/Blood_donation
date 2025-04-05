package blood_dontation.blood_api.model.DTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BloodRequestResponse<T> {
    private int statusCode;
    private String message;
    private T payLoad;

    public BloodRequestResponse(int statusCode, String message, T payLoad){
        this.statusCode = statusCode;
        this.message = message;
        this.payLoad = payLoad;
    }

}
