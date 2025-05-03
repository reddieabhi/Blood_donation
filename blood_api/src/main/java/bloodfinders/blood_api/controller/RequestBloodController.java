package bloodfinders.blood_api.controller;

import bloodfinders.blood_api.model.DTO.EventDetailsDTO;
import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.service.RequestBloodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/blood")
public class RequestBloodController {

    private static final Logger logger = LoggerFactory.getLogger(RequestBloodController.class);

    private final RequestBloodService requestBloodService;

    public RequestBloodController(RequestBloodService requestBloodService) {
        this.requestBloodService = requestBloodService;
    }

    @PostMapping("/request-new-event")
    public ResponseEntity<EventDetailsDTO> requestBlood(@RequestBody EventDetailsDTO eventDetailsDTO) {
        logger.info("Received new blood request {}", eventDetailsDTO);
        return requestBloodService.handleBloodRequest(eventDetailsDTO);
    }

    @GetMapping("/get-event/{id}")
    public ResponseEntity<EventDetailsDTO> getEvent(@PathVariable UUID id){
        return requestBloodService.getEvent(id);
    }

}
