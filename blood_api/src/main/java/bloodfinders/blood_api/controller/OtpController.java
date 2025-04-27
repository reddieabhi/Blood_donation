package bloodfinders.blood_api.controller;

import bloodfinders.blood_api.model.response.ApiResponse;
import bloodfinders.blood_api.service.OtpService;
import jakarta.mail.MessagingException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    // Generate OTP and send to email
    @PostMapping("/generate")
    public ApiResponse generateOtp(@RequestParam String email, @RequestParam Boolean find) throws MessagingException {
        return otpService.generateOtp(email, find);
    }

    // Verify OTP
    @PostMapping("/verify")
    public ApiResponse verifyOtp(@RequestParam String email, @RequestParam String otp) {
         return otpService.validateOtp(email, otp);
    }
}
