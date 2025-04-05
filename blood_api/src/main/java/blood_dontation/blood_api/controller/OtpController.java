package blood_dontation.blood_api.controller;

import blood_dontation.blood_api.service.OtpService;
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
    public Map<String, String> generateOtp(@RequestParam String email) {
        String otp = otpService.generateOtp(email);

        // In real implementation, send OTP via email
        System.out.println("Generated OTP for " + email + ": " + otp);

        Map<String, String> response = new HashMap<>();
        response.put("message", "OTP sent to " + email);
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
