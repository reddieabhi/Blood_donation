package blood_dontation.blood_api.controller;


import blood_dontation.blood_api.model.DTO.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class UserController {

    @RequestMapping("/register")
    public String create(User user){


        return "abc";
    }

    @RequestMapping("/login")
    public  String login(User user){

        // verify user.Username and user.password from DB.
        // return a token. (expiry 1 month)
        return "abc";
    }
}
