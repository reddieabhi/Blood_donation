package bloodfinders.blood_api.service;

import bloodfinders.blood_api.model.AcceptedRequest;
import bloodfinders.blood_api.model.response.RequestResponse;
import bloodfinders.blood_api.model.DTO.UserInfoDTO;
import bloodfinders.blood_api.model.Event;
import bloodfinders.blood_api.model.User;
import bloodfinders.blood_api.repository.AcceptedRequestRepository;
import bloodfinders.blood_api.repository.EventRepository;
import bloodfinders.blood_api.repository.UserRepository;
import bloodfinders.blood_api.fcm.FCMNotifications;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AcceptService {
    public final EventRepository eventRepository;
    public final UserRepository userRepository;
    public final AcceptedRequestRepository acceptedRequestRepository;


    AcceptService(EventRepository eventRepository, UserRepository userRepository, AcceptedRequestRepository acceptedRequestRepository){
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.acceptedRequestRepository = acceptedRequestRepository;
    }





    @Transactional
    public ResponseEntity<UserInfoDTO> handleRequest(UUID eid, UUID aid) {
        Optional<Event> eventOpt = eventRepository.findById(eid);
        Optional<User> acceptOpt = userRepository.findById(aid);

        if (eventOpt.isEmpty() || acceptOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Message", "User not found").body(null);

        }

        Event event = eventOpt.get();
        User acceptor = acceptOpt.get();
        UUID requesterId = event.getUser().getUid();


        // add new row in accepted requests table
        AcceptedRequest accepted = new AcceptedRequest(null, event.getEid(), acceptor.getUid(), requesterId);
        acceptedRequestRepository.save(accepted);


        // Notifying the user
        Optional<User> requesterOpt = userRepository.findById(requesterId);
        if (requesterOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Message", "Request not found").body(null);

        }

        User requester = requesterOpt.get();
        boolean sent = FCMNotifications.sendPushNotificationssingle(
                requester.getPushToken(), String.valueOf(acceptor.getUid()),
                "Someone from" + acceptor.getPlace() + "accepted your request");

        UserInfoDTO userInfoDTO = new UserInfoDTO(requester);

        if (sent){
//            return new RequestResponse<>(200, "Request accepted successfully sent token", userInfoDTO);
            return ResponseEntity.status(HttpStatus.OK).header("Message", "Request accepted successfully and push notification sent").body(userInfoDTO);

        } else {
//            return new RequestResponse<>(202, "Request accepted but failed to send push notification", userInfoDTO);
            return ResponseEntity.status(202).header("Message", "Request accepted but failed to send push notification").body(userInfoDTO);

        }


    }


    public ResponseEntity<UserInfoDTO> getAcceptor(UUID id) {
        Optional<AcceptedRequest> acceptedRequestOpt= acceptedRequestRepository.findById(id);

        if (acceptedRequestOpt.isEmpty()) {
//            return new RequestResponse<>(400, "No accepted entries found with following id", null);
            return ResponseEntity.status(202).header("Message", "No accepted entries found").body(null);


        }

        AcceptedRequest acceptedRequest = acceptedRequestOpt.get();
        UUID acceptorId = acceptedRequest.getAcceptorId();
        Optional<User> acceptorOpt = userRepository.findById(acceptorId);

        if (acceptorOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Message", "No Receiver/acceptor found").body(null);

        }
        User acceptor = acceptorOpt.get();
        UserInfoDTO userInfoDTO = new UserInfoDTO(acceptor);

//        return new RequestResponse<>(200, "Found acceptor", userInfoDTO);
        return ResponseEntity.status(HttpStatus.OK).header("Message", "Found Acceptor").body(userInfoDTO);

    }

    public ResponseEntity<List<UserInfoDTO>> getAllAcceptors(UUID eid) {
        List<AcceptedRequest> acceptedRequests = acceptedRequestRepository.findByEventId(eid);

        if (acceptedRequests.isEmpty()){
//            return new RequestResponse<>(404, "No acceptors found", null);
            return ResponseEntity.status(202).header("Message", "No Donors found").body(null);
        }

        List<UserInfoDTO> acceptors = new ArrayList<>();
        for (AcceptedRequest acceptedRequest : acceptedRequests){
            UUID acceptorId = acceptedRequest.getAcceptorId();

            Optional<User>  acceptorOpt = userRepository.findById(acceptorId);
            if (acceptorOpt.isPresent()){
                User acceptor = acceptorOpt.get();
                UserInfoDTO acceptorDTO = new  UserInfoDTO(acceptor);
                acceptors.add(acceptorDTO);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).header("Message", "Found donors").body(acceptors);

    }
}
