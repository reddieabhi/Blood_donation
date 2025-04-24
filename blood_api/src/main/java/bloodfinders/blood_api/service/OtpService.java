package bloodfinders.blood_api.service;

import bloodfinders.blood_api.email.EmailService;
import bloodfinders.blood_api.model.OtpVerification;
import bloodfinders.blood_api.model.User;
import bloodfinders.blood_api.model.response.OtpResponse;
import bloodfinders.blood_api.repository.OtpRepository;
import bloodfinders.blood_api.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);


    public OtpService(OtpRepository otpRepository, UserRepository userRepository) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;

        String smtpHost = "smtp.gmail.com";
        int smtpPort = 587;
        this.emailService = new EmailService(smtpHost, smtpPort);
    }

    // Generate OTP and store in DB
    public OtpResponse generateOtp(String email, Boolean find) throws MessagingException {

        OtpResponse otpResponse = new OtpResponse();
        if (find){
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()){
                otpResponse.setMessage(email + "not found");
                otpResponse.setStatusCode(404);
                return otpResponse;
            }
        }

        String otp = String.format("%06d", new Random().nextInt(1000000));
        logger.info("OTP generated {}", otp);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        OtpVerification otpVerification = new OtpVerification();
        otpVerification.setEmail(email);
        otpVerification.setOtpCode(otp);
        otpVerification.setExpiresAt(expiryTime);

        otpRepository.save(otpVerification);

        String subject = "Your One-Time Password (OTP)";
        String body = "Your OTP is: " + otp;
        emailService.sendEmail(email, subject, body);

        return otpResponse;
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
