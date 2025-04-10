package blood_dontation.blood_api.service;

import blood_dontation.blood_api.model.DTO.EventDetailsDTO;
import blood_dontation.blood_api.model.DTO.RequestResponse;
import blood_dontation.blood_api.model.Event;
import blood_dontation.blood_api.model.User;
import blood_dontation.blood_api.repository.EventRepository;

import java.util.Optional;
import java.util.UUID;

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
                event.getLocation(),
                user.getPlace()
        );


        return new RequestResponse<>(200, "Received event", eventDetailsDTO);
    }
}
