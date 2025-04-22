package bloodfinders.blood_api.service;

import bloodfinders.blood_api.model.DTO.EventDetailsDTO;
import bloodfinders.blood_api.model.DTO.RequestResponse;
import bloodfinders.blood_api.model.Event;
import bloodfinders.blood_api.model.User;
import bloodfinders.blood_api.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {
    EventRepository eventRepository;

    public  EventService(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    public RequestResponse<EventDetailsDTO> getEventById(UUID id) {
        Optional<Event> eventOpt = eventRepository.findById(id);

        if (eventOpt.isEmpty()){
            return new RequestResponse<>(404, "Event not found", null);
        }

        Event event = eventOpt.get();
        User user = event.getUser();

        EventDetailsDTO eventDetailsDTO = new EventDetailsDTO(
                user,
                event.getEid(),
                event.getBloodGroup(),
                event.getLocation().getX(),
                event.getLocation().getY(),
                user.getPlace(),
                event.getCurrentStatus()
        );


        return new RequestResponse<>(200, "Received event", eventDetailsDTO);
    }
}
