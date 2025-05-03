package bloodfinders.blood_api.controller;

import bloodfinders.blood_api.model.response.ApiResponse;
import bloodfinders.blood_api.service.OtpService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
public class OtpController {

    private final OtpService otpService;
    private static final Logger logger = LoggerFactory.getLogger(RequestBloodController.class);


    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    // Generate OTP and send to email
    @PostMapping("/generate")
    public ApiResponse generateOtp(@RequestParam String email, @RequestParam Boolean find) throws MessagingException {
        logger.info("Generating Otp to mail {}", email);
        return otpService.generateOtp(email, find);
    }

    // Verify OTP
    @PostMapping("/verify")
    public ApiResponse verifyOtp(@RequestParam String email, @RequestParam String otp) {
         return otpService.validateOtp(email, otp);
    }
}
