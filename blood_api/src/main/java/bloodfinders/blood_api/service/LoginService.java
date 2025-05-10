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

@Service
public class LoginService {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    public ResponseEntity<ApiResponse> userLogin(LoginRequest loginRequest) {
        Optional<User> useropt = userRepository.findByEmail(loginRequest.getEmail());

        if (useropt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Message", Constants.USER_NOT_FOUND).body(null);
        }

        User user = useropt.get();

        if (!user.getPassword().equals(loginRequest.getPassword())) {
//            return new RequestResponse<>(Constants.STATUS_INVALID, Constants.INVALID_PASSWORD, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Message", Constants.INVALID_PASSWORD).body(null);

        }


        String jwtToken = jwtUtil.generateToken(user.getUid(), Constants.EXPIRATION_TIME);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(Constants.LOGIN_SUCCESS);
        apiResponse.setStatusCode(Constants.STATUS_OK);
        apiResponse.setPayload("Login Successful");
        apiResponse.setJwtToken(jwtToken);

//        return new RequestResponse<>(Constants.STATUS_OK, Constants.LOGIN_SUCCESS, apiResponse);
        return ResponseEntity.status(HttpStatus.OK).header("Message", "Login Success").body(apiResponse);
    }


    public ResponseEntity<ApiResponse> passwordReset(LoginRequest loginRequest){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatusCode(Constants.STATUS_INTERNAL_SERVER_ERROR);
        apiResponse.setPayload("Username and Password cannot be empty");
        apiResponse.setMessage(Constants.UNABLE_TO_CHANGE_PASSWORD);

        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null || loginRequest.getEmail().isEmpty() || loginRequest.getPassword().isEmpty()){
//            return apiResponse;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Message", "Email or password cannot be empty").body(apiResponse);
        }

        Optional<User> useropt = userRepository.findByEmail(loginRequest.getEmail());

        if (useropt.isEmpty()){
            apiResponse.setStatusCode(Constants.STATUS_NOT_FOUND);
            apiResponse.setMessage("No user found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Message", "User not found").body(apiResponse);
        }

        User user = useropt.get();
        user.setPassword(loginRequest.getPassword());
        userRepository.save(user);
        apiResponse.setStatusCode(Constants.STATUS_OK);
        apiResponse.setMessage(Constants.PASSWORD_CHANGE_SUCCESSFULL);

        return ResponseEntity.status(HttpStatus.OK).header("Message", Constants.PASSWORD_CHANGE_SUCCESSFULL).body(apiResponse);
    }
}
