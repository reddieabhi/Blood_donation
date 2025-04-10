package blood_dontation.blood_api.service;

import blood_dontation.blood_api.model.AcceptedRequest;
import blood_dontation.blood_api.model.DTO.AcceptorInfoDTO;
import blood_dontation.blood_api.model.DTO.EventDetailsDTO;
import blood_dontation.blood_api.model.DTO.RequestResponse;
import blood_dontation.blood_api.model.DTO.UserPushInfo;
import blood_dontation.blood_api.model.Event;
import blood_dontation.blood_api.model.User;
import blood_dontation.blood_api.repository.AcceptedRequestRepository;
import blood_dontation.blood_api.repository.EventRepository;
import blood_dontation.blood_api.repository.UserRepository;
import blood_dontation.blood_api.utils.FCMNotifications;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public RequestResponse<Event> handleRequest(UUID eid, UUID aid) {
        Optional<Event> eventOpt = eventRepository.findById(eid);
        Optional<User> acceptOpt = userRepository.findById(aid);

        if (eventOpt.isEmpty() || acceptOpt.isEmpty()){
            return  new RequestResponse<>(500, "Event or acceptor not found", null);
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
            return  new RequestResponse<>(500, "Requester not found", null);
        }

        User requester = requesterOpt.get();
        boolean sent = FCMNotifications.sendPushNotificationssingle(
                requester.getPushToken(), String.valueOf(acceptor.getUid()),
                "Someone from" + acceptor.getPlace() + "accepted your request");


        if (sent){
            return new RequestResponse<>(200, "Request accepted successfully sent token", event);
        } else {
            return new RequestResponse<>(202, "Request accepted but failed to send push notification", event);
        }


    }

    public RequestResponse<EventDetailsDTO> getevent(UUID aid) {
        Optional<Event> eventsOpt = eventRepository.findById(aid);

        if (eventsOpt.isEmpty()){
            return new RequestResponse<>(400, "Event not found", null);
        }

        Event event = eventsOpt.get();

        EventDetailsDTO eventDetailsDTO = new EventDetailsDTO(event.getUser(), event.getEid(), event.getBloodGroup(),event.getLocation(), event.getPlace());


        return new RequestResponse<>(200, "Found event", eventDetailsDTO);
    }
}
