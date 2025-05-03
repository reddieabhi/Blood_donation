package bloodfinders.blood_api.service;

import bloodfinders.blood_api.constants.Constants;
import bloodfinders.blood_api.email.EmailService;
import bloodfinders.blood_api.jwt.JwtUtil;
import bloodfinders.blood_api.model.OtpEntity;
import bloodfinders.blood_api.model.User;
import bloodfinders.blood_api.model.response.ApiResponse;
import bloodfinders.blood_api.repository.OtpRepository;
import bloodfinders.blood_api.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private final JwtUtil jwtUtil;


    public OtpService(OtpRepository otpRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;

        String smtpHost = Constants.SMTP_HOST;
        int smtpPort = Constants.SMTP_PORT;
        this.emailService = new EmailService(smtpHost, smtpPort);
    }


    public ResponseEntity<ApiResponse> generateOtp(String email, Boolean find) throws MessagingException {

        ApiResponse otpResponse = new ApiResponse();
        if (find){
            logger.info("Find is true, hence assuming otp generating for a password reset and searching for user");
            Optional<User> userOpt = userRepository.findByEmail(email);

            if (userOpt.isEmpty()){
                otpResponse.setMessage(email + "not found");
                otpResponse.setStatusCode(Constants.STATUS_NOT_FOUND);
                logger.error("No user found with provided email");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(otpResponse);
            }
        }


        String otp = String.format("%0" + Constants.OTP_LENGTH + "d", new Random().nextInt((int) Math.pow(10, Constants.OTP_LENGTH)));
        logger.info("OTP generated {}", otp);
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(Constants.OTP_EXPIRY_MINUTES);

        OtpEntity otpEntity = otpRepository.findByEmail(email); //

        if (otpEntity != null) {
            otpEntity.setOtpCode(otp);
            otpEntity.setExpiresAt(expiryTime);
            otpEntity.setVerified(false);
        } else {
            otpEntity = otpEntityMaker(email, otp, expiryTime);
        }

        logger.info("Adding Otp entitity in DB {}", otpEntity);
        otpRepository.save(otpEntity);


        String subject = Constants.OTP_EMAIL_SUBJECT;
        String body = Constants.OTP_EMAIL_BODY_PREFIX + otp;

        try {
            emailService.sendEmail(email, subject, body);
        } catch (MessagingException e) {
            logger.error("Failed to send OTP email to {}", email, e);
            otpResponse.setMessage("Failed to send OTP email. Please try again later.");
            otpResponse.setStatusCode(Constants.STATUS_INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(otpResponse);
        }

        otpResponse.setMessage("OTP sent successfully to " + email);
        otpResponse.setStatusCode(Constants.STATUS_OK);
        logger.info("OTP sent successfully to {}", email);

        return ResponseEntity.status(HttpStatus.OK).body(otpResponse);
    }

    // Validate OTP
    public ResponseEntity<ApiResponse> validateOtp(String email, String otpCode) {
        Optional<OtpEntity> otpOpt = otpRepository.findByEmailAndVerifiedFalse(email);
        ApiResponse response = new ApiResponse();



        if (otpOpt.isPresent()) {
            OtpEntity otpVerification = otpOpt.get();

            if (otpVerification.getExpiresAt().isBefore(LocalDateTime.now())) {
                response.setMessage(Constants.OTP_EXPIRED);
                response.setStatusCode(400);
                response.setPayload(null);
                response.setJwtToken(null);
                return ResponseEntity.badRequest().body(response);
            }

            if (otpVerification.getOtpCode().equals(otpCode)) {
                logger.info("Valid otp, creating JWT and sending to user");
                otpVerification.setVerified(true);
                otpRepository.save(otpVerification);
                long EXPIRATION_TIME = 1000L * 60 * 5;
                String JwtToken = jwtUtil.generateToken(UUID.fromString(Constants.USER_FOR_VERIFIED_OTP_JWT), EXPIRATION_TIME);
                response.setMessage(Constants.OTP_VALID);
                response.setStatusCode(Constants.STATUS_OK);
                response.setPayload("Success");
                response.setJwtToken(JwtToken);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                logger.info("Invalid Otp");
                response.setMessage(Constants.INVALID_OTP);
                response.setStatusCode(Constants.STATUS_UNAUTHORIZED);
                response.setPayload(null);
                response.setJwtToken(null);
                return ResponseEntity.badRequest().body(response);
            }
        }

        logger.error("No otp delivered for provided email {}", email);
        response.setStatusCode(Constants.STATUS_NOT_FOUND);
        response.setMessage("No otp delivered for provided email" + email);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

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
