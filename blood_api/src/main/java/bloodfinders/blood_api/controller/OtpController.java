package bloodfinders.blood_api.controller;

import bloodfinders.blood_api.model.response.OtpResponse;
import bloodfinders.blood_api.service.OtpService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    // Generate OTP and send to email
    @PostMapping("/generate")
    public OtpResponse generateOtp(@RequestParam String email, @RequestParam Boolean find) {
        String otp = otpService.generateOtp(email, find);

        System.out.println("Generated OTP for " + email + ": " + otp);

        OtpResponse response = new OtpResponse();
        response.setMessage("OTP sent to " + email);
        response.setStatusCode(200);

        return response;
    }

    // Verify OTP
    @PostMapping("/verify")
    public Map<String, String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean isValid = otpService.validateOtp(email, otp);

        Map<String, String> response = new HashMap<>();
        response.put("message", isValid ? "OTP Verified!" : "Invalid or Expired OTP");
        return response;
    }
}
