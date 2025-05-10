package bloodfinders.blood_api.controller;

import bloodfinders.blood_api.constants.Constants;
import bloodfinders.blood_api.model.request.LoginRequest;
import bloodfinders.blood_api.model.response.ApiResponse;
import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.service.LoginService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        public ResponseEntity<ApiResponse> userLogin(LoginRequest loginRequest){
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null || loginRequest.getEmail().isEmpty() || loginRequest.getPassword().isEmpty()){
//                return new RequestResponse<>(Constants.STATUS_INVALID, Constants.EMAIL_PASSWORD_EMPTY, null);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Message", Constants.EMAIL_PASSWORD_EMPTY).body(null);
            }

        return loginService.userLogin(loginRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(LoginRequest loginRequest){
        return loginService.passwordReset(loginRequest);
    }

}
