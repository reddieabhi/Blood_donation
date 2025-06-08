package bloodfinders.blood_api.controller;

import bloodfinders.blood_api.model.DTO.EventDetailsDTO;
import bloodfinders.blood_api.model.DTO.Geo;
import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.service.RequestBloodService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PatchMapping("event/{eid}")
    public ResponseEntity<EventDetailsDTO>  updateEvent(@PathVariable UUID eid, @RequestBody EventDetailsDTO eventDetailsDTO){
        return requestBloodService.updateEvent(eid, eventDetailsDTO);
    }

    @GetMapping("/get-near-requests")
    public ResponseEntity<List<EventDetailsDTO>> getNearEvents(@RequestBody Geo geo){
        logger.debug("Incoming request to fetch near by events");
        if (geo == null || geo.getLatitude() == 0 || geo.getLongitude() == 0) {
            logger.info("Request body is null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        double lat =  geo.getLatitude();
        double longitude  = geo.getLongitude();
        logger.info("Incoming lat and long {}, {}",lat, longitude);

        return requestBloodService.getNearEvents(lat, longitude);
    }

    @GetMapping("/get-my-events/{id}")
    public ResponseEntity<List<EventDetailsDTO>> getMyEvents(@PathVariable UUID id){
        return requestBloodService.getMyEvents(id);
    }

    @GetMapping("/get-my-active-events/{id}")
    public ResponseEntity<List<EventDetailsDTO>> getMyActiveEvents(@PathVariable UUID id){
        return requestBloodService.getMyActiveEvents(id);
    }


}
