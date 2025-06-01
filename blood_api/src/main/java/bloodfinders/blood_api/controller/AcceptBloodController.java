package bloodfinders.blood_api.controller;


import bloodfinders.blood_api.model.AcceptedRequest;
import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.model.DTO.UserInfoDTO;
import bloodfinders.blood_api.service.AcceptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/blood")
public class AcceptBloodController {
    private static final Logger logger = LoggerFactory.getLogger(AcceptBloodController.class);

    private final AcceptService acceptService;


    public AcceptBloodController(AcceptService acceptService) {
        this.acceptService = acceptService;
    }

    @PostMapping("/accept/{eid}/{aid}")
    public ResponseEntity<UserInfoDTO> acceptRequest(@PathVariable UUID eid, @PathVariable UUID aid){

        return acceptService.handleRequest(eid, aid);
    }


    @GetMapping("/accept/{id}")
    public ResponseEntity<UserInfoDTO> getAcceptor(@PathVariable UUID id){

        return acceptService.getAcceptor(id);
    }


    @GetMapping("/acceptors/{eid}")
    public ResponseEntity<List<UserInfoDTO>> getAllAcceptors(@PathVariable UUID eid){
        return acceptService.getAllAcceptors(eid);
    }

    @GetMapping("/get-all-my-accepted/{id}")
    public ResponseEntity<List<AcceptedRequest>> getAllMyAccepted(@PathVariable UUID uid){
        logger.info("Received get all my accepted requests for id : {}", uid);
        return acceptService.getAllMyAccepted(uid);
    }

}
