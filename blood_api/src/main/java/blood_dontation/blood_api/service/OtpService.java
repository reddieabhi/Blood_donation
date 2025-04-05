package blood_dontation.blood_api.service;

import blood_dontation.blood_api.model.OtpVerification;
import blood_dontation.blood_api.repository.OtpRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    private final OtpRepository otpRepository;

    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    // Generate OTP and store in DB
    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5); // OTP valid for 5 minutes

        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtpCode(otp);
        otpVerification.setExpiresAt(expiryTime);

        otpRepository.save(otpVerification);
        return otp;
    }

    // Validate OTP
    public boolean validateOtp(String email, String otpCode) {
        Optional<OtpVerification> otpOpt = otpRepository.findByEmailAndVerifiedFalse(email);

        if (otpOpt.isPresent()) {
            OtpVerification otpVerification = otpOpt.get();

            if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
                return false; // OTP expired
            }

            if (otpVerification.getOtpCode().equals(otpCode)) {
                otpVerification.setVerified(true);
                otpRepository.save(otpVerification);
                return true; // OTP valid
            }
        }
        return false; // Invalid OTP
    }

    // Delete expired OTPs (Can be scheduled as a cleanup job)
    public void deleteExpiredOtps() {
        otpRepository.deleteAll(otpRepository.findAll().stream()
                .filter(otp -> otp.getExpiresAt().isBefore(LocalDateTime.now()))
                .toList());
    }
}
