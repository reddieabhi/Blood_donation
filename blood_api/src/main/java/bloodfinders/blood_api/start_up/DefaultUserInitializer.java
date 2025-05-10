//package bloodfinders.blood_api.start_up;
//
//import bloodfinders.blood_api.controller.RequestBloodController;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import bloodfinders.blood_api.model.User;
//import bloodfinders.blood_api.repository.UserRepository;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.beans.factory.annotation.Value;
//
//import java.util.UUID;
//
//@Component
//public class DefaultUserInitializer implements ApplicationRunner {
//
//    private static final Logger logger = LoggerFactory.getLogger(DefaultUserInitializer.class);
//
//
//    @Value("${app.default-user-id}")
//    private String defaultUserId;
//
//    private final UserRepository userRepository;
//
//    public DefaultUserInitializer(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public void run(ApplicationArguments args) {
//
//        logger.warn("Default user to be inserted in users {}", defaultUserId);
//        userRepository.findById(UUID.fromString(defaultUserId)).ifPresentOrElse(
//                user -> {
//
//                    user.setName("Jwtauthuser");
//                    userRepository.save(user);
//                },
//                () -> {
//                    User newUser = new User();
//                    newUser.setUid(UUID.fromString(defaultUserId));
//                    newUser.setName("Jwtauthuser");
//                    newUser.setEmail("");
//                    userRepository.save(newUser);
//                }
//        );
//    }
//}
