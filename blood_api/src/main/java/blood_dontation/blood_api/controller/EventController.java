package blood_dontation.blood_api.controller;


import blood_dontation.blood_api.model.DTO.EventDetailsDTO;
import blood_dontation.blood_api.model.DTO.RequestResponse;
import blood_dontation.blood_api.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService){
        this.eventService = eventService;
    }



    @GetMapping("/{id}")
    public RequestResponse<EventDetailsDTO> getUserById(@PathVariable UUID id) {
        return eventService.getEventById(id);
    }
}
