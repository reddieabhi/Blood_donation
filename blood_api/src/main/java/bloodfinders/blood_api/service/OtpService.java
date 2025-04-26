package bloodfinders.blood_api.service;

import bloodfinders.blood_api.constants.Constants;
import bloodfinders.blood_api.email.EmailService;
import bloodfinders.blood_api.model.OtpEntity;
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

        String smtpHost = Constants.SMTP_HOST;
        int smtpPort = Constants.SMTP_PORT;
        this.emailService = new EmailService(smtpHost, smtpPort);
    }


    public OtpResponse generateOtp(String email, Boolean find) throws MessagingException {

        OtpResponse otpResponse = new OtpResponse();
        if (find){
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()){
                otpResponse.setMessage(email + "not found");
                otpResponse.setStatusCode(Constants.STATUS_NOT_FOUND);
                return otpResponse;
            }
        }

        String otp = String.format("%0" + Constants.OTP_LENGTH + "d", new Random().nextInt((int) Math.pow(10, Constants.OTP_LENGTH)));
        logger.info("OTP generated {}", otp);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(Constants.OTP_EXPIRY_MINUTES);

        OtpEntity otpEntity = otpRepository.findByEmail(email); // <- find existing

        if (otpEntity != null) {
            otpEntity.setOtpCode(otp);
            otpEntity.setExpiresAt(expiryTime);
            otpEntity.setVerified(false);  // maybe reset verified if needed
        } else {

            otpEntity = otpEntityMaker(email, otp, expiryTime);
        }

        otpRepository.save(otpEntity);


        String subject = Constants.OTP_EMAIL_SUBJECT;
        String body = Constants.OTP_EMAIL_BODY_PREFIX + otp;

        try {
            emailService.sendEmail(email, subject, body);
        } catch (MessagingException e) {
            logger.error("Failed to send OTP email to {}", email, e);
            otpResponse.setMessage("Failed to send OTP email. Please try again later.");
            otpResponse.setStatusCode(Constants.STATUS_INTERNAL_SERVER_ERROR);
            return otpResponse;
        }

        otpResponse.setMessage("OTP sent successfully to " + email);
        otpResponse.setStatusCode(Constants.STATUS_OK);

        return otpResponse;
    }

    // Validate OTP
    public boolean validateOtp(String email, String otpCode) {
        Optional<OtpEntity> otpOpt = otpRepository.findByEmailAndVerifiedFalse(email);

        if (otpOpt.isPresent()) {
            OtpEntity otpVerification = otpOpt.get();

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

    public OtpEntity otpEntityMaker(String email, String otp, LocalDateTime expTime){
        OtpEntity otpEntity = new OtpEntity();
        otpEntity.setEmail(email);
        otpEntity.setOtpCode(otp);
        otpEntity.setExpiresAt(expTime);

        return  otpEntity;

    }

    // Delete expired OTPs (Can be scheduled as a cleanup job)
    public void deleteExpiredOtps() {
        otpRepository.deleteAll(otpRepository.findAll().stream()
                .filter(otp -> otp.getExpiresAt().isBefore(LocalDateTime.now()))
                .toList());
    }
}
