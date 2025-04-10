package blood_dontation.blood_api.controller;

import blood_dontation.blood_api.model.DTO.RequestResponse;
import blood_dontation.blood_api.model.Event;
import blood_dontation.blood_api.service.RequestBloodService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/blood")
public class RequestBloodController {

    private final RequestBloodService requestBloodService;

    public RequestBloodController(RequestBloodService requestBloodService) {
        this.requestBloodService = requestBloodService;
    }

    @PostMapping("/request")
    public RequestResponse<Event> requestBlood(@RequestParam double lat,
                                               @RequestParam double lng,
                                               @RequestParam String bloodGroup,
                                               @RequestParam UUID userId,
                                               @RequestParam String place) {
        System.out.printf("=================Lat: %f, Lng: %f, Blood Group: %s, User ID: %s%n", lat, lng, bloodGroup, userId);
        return requestBloodService.handleBloodRequest(lat, lng, bloodGroup, userId);
    }
}
