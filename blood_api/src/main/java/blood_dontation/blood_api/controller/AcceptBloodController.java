package blood_dontation.blood_api.controller;


import blood_dontation.blood_api.model.DTO.EventDetailsDTO;
import blood_dontation.blood_api.model.DTO.RequestResponse;
import blood_dontation.blood_api.model.Event;
import blood_dontation.blood_api.service.AcceptService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/blood")
public class AcceptBloodController {
    private final AcceptService acceptService;


    public AcceptBloodController(AcceptService acceptService) {
        this.acceptService = acceptService;
    }

    @PostMapping("/accept/{eid}/{aid}")
    public RequestResponse<Event> acceptRequestResponse(@PathVariable UUID eid, @PathVariable UUID aid){

        return acceptService.handleRequest(eid, aid);
    }

    @GetMapping("/accept/{eid}/{aid}")
    public RequestResponse<EventDetailsDTO> getone(@PathVariable UUID aid){
        return  acceptService.getevent(aid);
    }


}
