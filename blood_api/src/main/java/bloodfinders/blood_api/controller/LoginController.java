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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.module.Configuration;

@RestController
    @RequestMapping("/user")
    public class LoginController {

        private final LoginService loginService;

        private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

        public LoginController(LoginService loginService){
            this.loginService = loginService;
        }

        @PostMapping("/login")
        public ResponseEntity<ApiResponse> userLogin(@RequestBody  LoginRequest loginRequest){
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null || loginRequest.getEmail().isEmpty() || loginRequest.getPassword().isEmpty()){
                logger.info("Login request received for email {}", loginRequest.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Message", Constants.EMAIL_PASSWORD_EMPTY).body(null);
            }

        return loginService.userLogin(loginRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody LoginRequest loginRequest){

        logger.info("Reset password request received for email {}", loginRequest.getEmail());
        return loginService.passwordReset(loginRequest);
    }

}
