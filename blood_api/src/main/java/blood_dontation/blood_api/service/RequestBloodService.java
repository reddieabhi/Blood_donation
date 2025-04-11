package blood_dontation.blood_api.service;

import blood_dontation.blood_api.constants.Constants;
import blood_dontation.blood_api.model.DTO.EventDetailsDTO;
import blood_dontation.blood_api.model.DTO.RequestResponse;
import blood_dontation.blood_api.model.DTO.UserPushInfo;
import blood_dontation.blood_api.model.Event;
import blood_dontation.blood_api.model.User;
import blood_dontation.blood_api.repository.EventRepository;
import blood_dontation.blood_api.utils.FCMNotifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RequestBloodService {

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.search-radius-km}")
    private double searchRadiusKm;

    private final EventRepository eventRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public RequestBloodService(EventRepository eventRepository) {

        this.eventRepository = eventRepository;
    }

    @Transactional
    public RequestResponse<EventDetailsDTO> handleBloodRequest(EventDetailsDTO eventDetailsDTO) {
        // Find nearby users (fetch only userId & pushToken)
//        double lat, double lng, String bloodGroup, UUID userId
        double lat = eventDetailsDTO.getLatitude();
        double lng = eventDetailsDTO.getLongitude();
        String bloodGroup = eventDetailsDTO.getBloodGroup();
        UUID userId = eventDetailsDTO.getUserId();
        String currentStatus = eventDetailsDTO.getCurrentStatus();
        List<UserPushInfo> nearbyUsers = findNearbyUsers(lat, lng, bloodGroup);

        // Create event entry in DB
        Point location = geometryFactory.createPoint(new Coordinate(lng, lat));


        User user = entityManager.find(User.class, userId);
        if (user == null) {
            return  new RequestResponse<>(404, "User not found", null);
        }

        Event event = new Event();
        event.setUser(user);
        event.setBloodGroup(bloodGroup);
        event.setLocation(location);
        event.setCurrentStatus(Constants.EVENT_STATUS_CREATED);
        eventRepository.save(event);

        eventDetailsDTO = getEventDetailsDTOfromEvent(event);

        // Send push notifications to nearby users
        String msg = "Urgent Blood Request! A request for blood group " + bloodGroup + " is nearby.";
        if (!nearbyUsers.isEmpty()) {
            boolean sent = FCMNotifications.sendPushNotifications(nearbyUsers, bloodGroup, msg, event.getUser().getUid().toString());
            if (!sent){
                return  new RequestResponse<>(500, "Event created but push notifications not sent", eventDetailsDTO);
            }
        }
        return new RequestResponse<>(200, "Event created successfully", eventDetailsDTO);
    }

    public List<UserPushInfo> findNearbyUsers(double lat, double lng, String bloodGroup) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserPushInfo> query = cb.createQuery(UserPushInfo.class);
        Root<User> user = query.from(User.class);

        Predicate withinDistance = cb.isTrue(cb.function("ST_DWithin", Boolean.class,
                user.get("location"), cb.function("ST_SetSRID", Object.class,
                        cb.function("ST_MakePoint", Object.class, cb.literal(lng), cb.literal(lat)), cb.literal(4326)),
                cb.literal(searchRadiusKm * 1000)
        ));

        Predicate bloodGroupMatch = cb.equal(user.get("bloodGroup"), bloodGroup);

        query.select(cb.construct(UserPushInfo.class, user.get("uid"), user.get("pushToken")))
                .where(cb.and(withinDistance, bloodGroupMatch));

        return entityManager.createQuery(query).getResultList();
    }


    public EventDetailsDTO getEventDetailsDTOfromEvent(Event event){
        EventDetailsDTO dto = new EventDetailsDTO(
                event.getUser(),
                event.getEid(),
                event.getBloodGroup(),
                event.getLocation().getY(),  // latitude
                event.getLocation().getX(),  // longitude
                event.getPlace(),
                event.getCurrentStatus()
        );

        return dto;
    }

    public RequestResponse<EventDetailsDTO> getEvent(UUID id) {
        Optional<Event> eventOpt = eventRepository.findById(id);

        if (eventOpt.isEmpty()){
            return new RequestResponse<>(400, "No event found", null);
        }
        Event event = eventOpt.get();
        EventDetailsDTO eventDetailsDTO = getEventDetailsDTOfromEvent(event);
        return new RequestResponse<>(200, "Found event", eventDetailsDTO);
    }
}
