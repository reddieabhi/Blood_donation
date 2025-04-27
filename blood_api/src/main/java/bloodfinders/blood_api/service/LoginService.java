package bloodfinders.blood_api.service;


import bloodfinders.blood_api.constants.Constants;
import bloodfinders.blood_api.jwt.JwtUtil;
import bloodfinders.blood_api.model.User;
import bloodfinders.blood_api.model.request.LoginRequest;
import bloodfinders.blood_api.model.response.ApiResponse;
import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.repository.UserRepository;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;

    public RequestResponse<ApiResponse> userLogin(LoginRequest loginRequest) {
        Optional<User> useropt = userRepository.findByEmail(loginRequest.getEmail());

        if (useropt.isEmpty()){
            return new RequestResponse<>(Constants.STATUS_NOT_FOUND, Constants.USER_NOT_FOUND, null);
        }

        User user = useropt.get();

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            return new RequestResponse<>(Constants.STATUS_INVALID, Constants.INVALID_PASSWORD, null);
        }


        String jwtToken = jwtUtil.generateToken(user.getUid(), Constants.EXPIRATION_TIME);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setMessage(Constants.LOGIN_SUCCESS);
        apiResponse.setStatusCode(Constants.STATUS_OK);
        apiResponse.setPayload("Login Successful");
        apiResponse.setJwtToken(jwtToken);

        return new RequestResponse<>(Constants.STATUS_OK, Constants.LOGIN_SUCCESS, apiResponse);
    }


    public ApiResponse passwordReset(LoginRequest loginRequest){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatusCode(Constants.STATUS_INTERNAL_SERVER_ERROR);
        apiResponse.setPayload("Username and Password cannot be empty");
        apiResponse.setMessage(Constants.UNABLE_TO_CHANGE_PASSWORD);

        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null || loginRequest.getEmail().isEmpty() || loginRequest.getPassword().isEmpty()){
            return apiResponse;
        }

        Optional<User> useropt = userRepository.findByEmail(loginRequest.getEmail());

        if (useropt.isEmpty()){
            apiResponse.setStatusCode(Constants.STATUS_NOT_FOUND);
            apiResponse.setMessage("No user found");
            return apiResponse;
        }

        User user = useropt.get();
        user.setPassword(loginRequest.getPassword());
        userRepository.save(user);
        apiResponse.setStatusCode(Constants.STATUS_OK);
        apiResponse.setMessage(Constants.PASSWORD_CHANGE_SUCCESSFULL);

        return apiResponse;
    }
}
