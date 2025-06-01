package bloodfinders.blood_api.controller;

import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.model.DTO.UserInfoDTO;
import bloodfinders.blood_api.model.request.UserRegisterDTO;
import bloodfinders.blood_api.model.response.UserRegisterLoginDTO;
import bloodfinders.blood_api.service.OtpService;
import bloodfinders.blood_api.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @PostMapping("/register")
    public ResponseEntity<UserRegisterLoginDTO> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
        logger.info("Registering new user {}", userRegisterDTO);
        return  userService.registerUser(userRegisterDTO);
    }

    @PatchMapping("/update")
    public ResponseEntity<UserInfoDTO> updateUser(@RequestBody UserRegisterDTO userRegisterDTO, @RequestParam UUID uuid) {
        if (userRegisterDTO.getPassword() != null && !userRegisterDTO.getPassword().isBlank()) {
//            return new RequestResponse<UserInfoDTO>(400, "Password not allowed to change here", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("message", "Password not allowed to change here").body(null);
        }

        return userService.updateUser(uuid,userRegisterDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDTO> getUser(@PathVariable String id){
        if (id.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("message", "User ID cannot be null").body(null);
        }
        logger.info("Get request for user id : {}", id);
        return userService.getUser(id);

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
