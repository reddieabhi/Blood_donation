package blood_dontation.blood_api.controller;

import blood_dontation.blood_api.model.DTO.EventDetailsDTO;
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

    @PostMapping("/request-new-event")
    public RequestResponse<EventDetailsDTO> requestBlood(@RequestBody EventDetailsDTO eventDetailsDTO) {
        System.out.println(eventDetailsDTO);
        return requestBloodService.handleBloodRequest(eventDetailsDTO);
    }

    @GetMapping("/get-event/{id}")
    public RequestResponse<EventDetailsDTO> getEvent(@PathVariable UUID id){
        return requestBloodService.getEvent(id);
    }

}
