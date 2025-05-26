package bloodfinders.blood_api.service;


import bloodfinders.blood_api.constants.Constants;
import bloodfinders.blood_api.jwt.JwtUtil;
import bloodfinders.blood_api.model.User;
import bloodfinders.blood_api.model.request.LoginRequest;
import bloodfinders.blood_api.model.response.ApiResponse;
import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.repository.UserRepository;
import lombok.extern.java.Log;
import org.checkerframework.checker.units.qual.C;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    public LoginService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<ApiResponse> userLogin(LoginRequest loginRequest) {
        Optional<User> useropt = userRepository.findByEmail(loginRequest.getEmail());

        if (useropt.isEmpty()){
            logger.info("User not found with provided email {}",loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Message", Constants.USER_NOT_FOUND).body(null);
        }

        User user = useropt.get();
        logger.info("User found in db {}", user.getUserName());
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            logger.warn("Invalid password for email {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Message", Constants.INVALID_PASSWORD).body(null);

        }


        String jwtToken = jwtUtil.generateToken(user.getUid(), Constants.EXPIRATION_TIME);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(Constants.LOGIN_SUCCESS);
        apiResponse.setStatusCode(Constants.STATUS_OK);
        apiResponse.setPayload("Login Successful");
        apiResponse.setJwtToken(jwtToken);
        logger.info("Login success for email {}", loginRequest.getEmail());
        logger.info("Created jwt for email {}: jwt : {}", loginRequest.getEmail(), jwtToken);

        return ResponseEntity.status(HttpStatus.OK).header("Message", "Login Success").body(apiResponse);
    }


    public ResponseEntity<ApiResponse> passwordReset(LoginRequest loginRequest){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatusCode(Constants.STATUS_INTERNAL_SERVER_ERROR);
        apiResponse.setPayload("Username and Password cannot be empty");
        apiResponse.setMessage(Constants.UNABLE_TO_CHANGE_PASSWORD);

        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null || loginRequest.getEmail().isEmpty() || loginRequest.getPassword().isEmpty()){
            logger.info("Email or password cannot be empty");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Message", "Email or password cannot be empty").body(apiResponse);
        }

        Optional<User> useropt = userRepository.findByEmail(loginRequest.getEmail());

        if (useropt.isEmpty()){
            apiResponse.setStatusCode(Constants.STATUS_NOT_FOUND);
            apiResponse.setMessage("No user found");
            logger.info("No user found for email {}to update password", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Message", "User not found").body(apiResponse);
        }

        User user = useropt.get();
        user.setPassword(loginRequest.getPassword());
        userRepository.save(user);
        apiResponse.setStatusCode(Constants.STATUS_OK);
        apiResponse.setMessage(Constants.PASSWORD_CHANGE_SUCCESSFULL);
        apiResponse.setPayload(Constants.PASSWORD_CHANGE_SUCCESSFULL);
        logger.info("Password changed successfully for email {}", loginRequest.getEmail());

        return ResponseEntity.status(HttpStatus.OK).header("Message", Constants.PASSWORD_CHANGE_SUCCESSFULL).body(apiResponse);
    }
}
