//package blood_dontation.blood_api.service;
//
////import blood_dontation.blood_api.jwt.JwtUtil;
//import blood_dontation.blood_api.model.DTO.RequestResponse;
//import blood_dontation.blood_api.model.DTO.UserInfoDTO;
//import blood_dontation.blood_api.model.DTO.UserRegisterDTO;
//import blood_dontation.blood_api.model.DTO.UserRegisterLoginDTO;
//import blood_dontation.blood_api.model.User;
//import blood_dontation.blood_api.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.locationtech.jts.geom.Coordinate;
//import org.locationtech.jts.geom.GeometryFactory;
//import org.locationtech.jts.geom.Point;
//import org.springframework.stereotype.Service;
//
//import java.util.UUID;
//
//
//@Service
//@RequiredArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//    private final GeometryFactory geometryFactory = new GeometryFactory();
////    private final JwtUtil jwtUtil;
//
//    public RequestResponse<UserRegisterLoginDTO> registerUser(UserRegisterDTO userRegisterDTO) {
//        User user = new User();
//        copyToUser(userRegisterDTO, user);
//        user = userRepository.save(user);
//        String JwtToken = jwtUtil.generateToken(user.getUid());
//        return new RequestResponse<>(202, "User Registered successfully", new UserRegisterLoginDTO(user.getUid(),JwtToken));
//    }
//
//    public RequestResponse<UserInfoDTO> updateUser(UUID uid, UserRegisterDTO dto, String token) {
//            User user = userRepository.findById(uid)
//                    .orElseThrow(() -> new RuntimeException("User not found"));
//
//
//            if (dto.getUserName() != null) user.setUserName(dto.getUserName());
//            if (dto.getName() != null) user.setName(dto.getName());
//            if (dto.getPlace() != null) user.setPlace(dto.getPlace());
//            if (dto.getDOB() != null) user.setDOB(dto.getDOB());
//            if (dto.getEmail() != null) user.setEmail(dto.getEmail());
//            if (dto.getCity() != null) user.setCity(dto.getCity());
//            if (dto.getWeight() != 0) user.setWeight(dto.getWeight());
//            if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
//            if (dto.getBloodGroup() != null) user.setBloodGroup(dto.getBloodGroup());
//            if (dto.getPushToken() != null) user.setPushToken(dto.getPushToken());
//
//            if (dto.getLatitude() != 0.0 && dto.getLongitude() != 0.0) {
//                Point location = geometryFactory.createPoint(new Coordinate(dto.getLatitude(), dto.getLongitude()));
//                user.setLocation(location);
//            }
//
//             userRepository.save(user);
//
//            return new RequestResponse<>(200, "User updated succesfully", new UserInfoDTO(user));
//
//        }
//
//
////        public UserDTO getUserByToken(String token) {
////        UUID userId = extractUserIdFromToken(token);
////        User user = userRepository.findById(userId)
////                .orElseThrow(() -> new RuntimeException("User not found"));
////
////        return new UserDTO(user, token);
////    }
//
////    public void deleteUser(String token) {
////        UUID userId = extractUserIdFromToken(token);
////        userRepository.deleteById(userId);
////    }
//
////    private UUID extractUserIdFromToken(String token) {
////        String cleaned = token.replace("Bearer ", "").trim();
////        String userIdStr = jwtUtil.extractUserId(cleaned);
////        return UUID.fromString(userIdStr);
////    }
//
//    private void copyToUser(UserRegisterDTO dto, User user) {
//        user.setUserName(dto.getUserName());
//        user.setPassword(dto.getPassword());
//        user.setName(dto.getName());
//        user.setPlace(dto.getPlace());
//        user.setDOB(dto.getDOB());
//        user.setEmail(dto.getEmail());
//        user.setCity(dto.getCity());
//        user.setWeight(dto.getWeight());
//        user.setPhoneNumber(dto.getPhoneNumber());
//        user.setBloodGroup(dto.getBloodGroup());
//        user.setPushToken(dto.getPushToken());
//        // location can also be set here if needed
//        Point location = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
//        user.setLocation(location);
//
//    }
//}
