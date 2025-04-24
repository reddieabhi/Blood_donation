package bloodfinders.blood_api.service;

import bloodfinders.blood_api.constants.Constants;
import bloodfinders.blood_api.model.DTO.EventDetailsDTO;
import bloodfinders.blood_api.model.DTO.RequestResponse;
import bloodfinders.blood_api.model.DTO.UserPushTokenDTO;
import bloodfinders.blood_api.model.Event;
import bloodfinders.blood_api.model.User;
import bloodfinders.blood_api.repository.EventRepository;
import bloodfinders.blood_api.fcm.FCMNotifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RequestBloodService {

    private static final Logger logger = LoggerFactory.getLogger(RequestBloodService.class);

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

        double lat = eventDetailsDTO.getLatitude();
        double lng = eventDetailsDTO.getLongitude();
        String bloodGroup = eventDetailsDTO.getBloodGroup();
        UUID userId = eventDetailsDTO.getUserId();
        String currentStatus = eventDetailsDTO.getCurrentStatus();
        logger.debug("Lat {}, long {}, bloodGroup {}, userId {}, currentStatus {}", lat, lng, bloodGroup, userId, currentStatus);
        List<UserPushTokenDTO> nearbyUsers = findNearbyUsers(lat, lng);
        logger.info("Found {} nearby users, sending notifications for them", nearbyUsers.size());

        int limit = Math.min(10, nearbyUsers.size());
        logger.debug("Top {} nearby users:", limit);

        for (int i = 0; i < limit; i++) {
            UserPushTokenDTO user = nearbyUsers.get(i);
            logger.debug("User {}: PushToken {}", user.getUserId(), user.getPushToken());
        }

        // Create event entry in DB
        Point location = geometryFactory.createPoint(new Coordinate(lng, lat));
        logger.debug("lat long to location : {}", location);

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

    public List<UserPushTokenDTO> findNearbyUsers(double lat, double lng) {
        logger.debug("Started finding nearby users. Lat: {}, Lng: {}", lat, lng);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserPushTokenDTO> query = cb.createQuery(UserPushTokenDTO.class);
        Root<User> user = query.from(User.class);

        Predicate withinDistance = cb.isTrue(cb.function("ST_DWithin", Boolean.class,
                user.get("location"), cb.function("ST_SetSRID", Object.class,
                        cb.function("ST_MakePoint", Object.class, cb.literal(lng), cb.literal(lat)), cb.literal(4326)),
                cb.literal(searchRadiusKm * 1000)
        ));

        logger.debug("Distance condition created using ST_DWithin for coordinates ({}, {}) with radius {} meters", lng, lat, searchRadiusKm * 1000);

        query.select(cb.construct(UserPushTokenDTO.class, user.get("uid"), user.get("pushToken")))
                .where(withinDistance);

        logger.info("Created query for event {}", query);

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
