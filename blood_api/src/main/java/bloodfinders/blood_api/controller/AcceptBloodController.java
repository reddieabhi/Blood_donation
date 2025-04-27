package bloodfinders.blood_api.controller;


import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.model.DTO.UserInfoDTO;
import bloodfinders.blood_api.service.AcceptService;
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
