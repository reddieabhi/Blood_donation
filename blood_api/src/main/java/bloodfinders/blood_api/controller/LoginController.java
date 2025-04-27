package bloodfinders.blood_api.controller;

import bloodfinders.blood_api.constants.Constants;
import bloodfinders.blood_api.model.request.LoginRequest;
import bloodfinders.blood_api.model.response.ApiResponse;
import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.service.LoginService;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.module.Configuration;

@RestController
@RequestMapping("/user")
public class LoginController {

    private LoginService loginService;

    @PostMapping("/login")
    public RequestResponse<ApiResponse> userLogin(LoginRequest loginRequest){
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null || loginRequest.getEmail().isEmpty() || loginRequest.getPassword().isEmpty()){
            return new RequestResponse<>(Constants.STATUS_INVALID, Constants.EMAIL_PASSWORD_EMPTY, null);
        }

        return loginService.userLogin(loginRequest);
    }

    @PostMapping("/reset-password")
    public ApiResponse resetPassword(LoginRequest loginRequest){
        return loginService.passwordReset(loginRequest);
    }

}
