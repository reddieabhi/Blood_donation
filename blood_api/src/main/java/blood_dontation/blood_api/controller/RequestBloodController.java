package blood_dontation.blood_api.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/blood-request")
public class RequestBloodController {

    @PostMapping
    @RequestMapping("/request")
    public String requestBlood(@RequestParam double lat,
                               @RequestParam double lng,
                               @RequestParam String uid,
                               @RequestParam String bloodGroup) {

        // go to service find near users and send push notifications.
        return "Blood request received for UID: " + uid + " at location: (" + lat + ", " + lng + ")";
    }


}
