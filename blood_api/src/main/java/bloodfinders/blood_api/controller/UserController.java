package bloodfinders.blood_api.controller;

import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.model.DTO.UserInfoDTO;
import bloodfinders.blood_api.model.request.UserRegisterDTO;
import bloodfinders.blood_api.model.response.UserRegisterLoginDTO;
import bloodfinders.blood_api.service.OtpService;
import bloodfinders.blood_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @PostMapping("/register")
    public RequestResponse<UserRegisterLoginDTO> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        logger.info("Registering new user {}", userRegisterDTO);
        return  userService.registerUser(userRegisterDTO);
    }

    @PatchMapping("/update")
    public RequestResponse<UserInfoDTO> updateUser(@RequestBody UserRegisterDTO userRegisterDTO, @RequestBody UUID uid,
                                        @RequestHeader("Authorization") String token) {
        if (userRegisterDTO.getPassword() != null && !userRegisterDTO.getPassword().isBlank()) {
            return new RequestResponse<UserInfoDTO>(400, "Password not allowed to change here", null);
        }

        return userService.updateUser(uid,userRegisterDTO, token);
    }


//    @GetMapping("/me")
//    public ResponseEntity<UserInfoDTO> getUser(@RequestHeader("Authorization") String token) {
//        UserInfoDTO userDTO = userService.getUserByToken(token);
//         ResponseEntity.ok(userDTO);
//    }

//    @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token) {
//        userService.deleteUser(token);
//        return ResponseEntity.ok("User deleted successfully");
//    }
}
