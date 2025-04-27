package bloodfinders.blood_api.controller;


import bloodfinders.blood_api.model.DTO.EventDetailsDTO;
import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.service.EventService;
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
