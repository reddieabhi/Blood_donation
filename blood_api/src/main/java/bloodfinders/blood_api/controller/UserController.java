package bloodfinders.blood_api.controller;

import bloodfinders.blood_api.model.DTO.RequestResponse;
import bloodfinders.blood_api.model.DTO.UserInfoDTO;
import bloodfinders.blood_api.model.DTO.UserRegisterDTO;
import bloodfinders.blood_api.model.DTO.UserRegisterLoginDTO;
import bloodfinders.blood_api.model.User;
import bloodfinders.blood_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public RequestResponse<UserRegisterLoginDTO> registerUser(@RequestBody UserRegisterDTO userRegisterDTO) {
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
