package blood_dontation.blood_api.controller;


import blood_dontation.blood_api.model.DTO.EventDetailsDTO;
import blood_dontation.blood_api.model.DTO.RequestResponse;
import blood_dontation.blood_api.model.DTO.UserInfoDTO;
import blood_dontation.blood_api.model.Event;
import blood_dontation.blood_api.model.User;
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
    public RequestResponse<UserInfoDTO> acceptRequest(@PathVariable UUID eid, @PathVariable UUID aid){

        return acceptService.handleRequest(eid, aid);
    }


    @GetMapping("/accept/{id}")
    public RequestResponse<UserInfoDTO> getAcceptor(@PathVariable UUID id){

        return acceptService.getAcceptor(id);
    }


    @GetMapping("/acceptors/{eid}")
    public RequestResponse<List<UserInfoDTO>> getAllAcceptors(@PathVariable UUID eid){
        return acceptService.getAllAcceptors(eid);
    }


}
