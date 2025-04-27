package bloodfinders.blood_api.model.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestResponse<T> {
    private int statusCode;
    private String message;
    private T payLoad;

    public RequestResponse(int statusCode, String message, T payLoad){
        this.statusCode = statusCode;
        this.message = message;
        this.payLoad = payLoad;
    }

}
