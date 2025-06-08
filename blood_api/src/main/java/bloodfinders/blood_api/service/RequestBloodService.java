package bloodfinders.blood_api.service;

import bloodfinders.blood_api.constants.Constants;
import bloodfinders.blood_api.model.DTO.EventDetailsDTO;
import bloodfinders.blood_api.model.DTO.UserPushTokenDTO;
import bloodfinders.blood_api.model.Event;
import bloodfinders.blood_api.model.User;
import bloodfinders.blood_api.repository.EventRepository;
import bloodfinders.blood_api.fcm.FCMNotifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public ResponseEntity<EventDetailsDTO> handleBloodRequest(EventDetailsDTO eventDetailsDTO) {

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
//            return  new RequestResponse<>(404, "User not found", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Message", "User not found").body(null);
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
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).header("Message", "Event created but push notifications not sent").body(eventDetailsDTO);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).header("Message", "Event created Successfully").body(eventDetailsDTO);
    }

    public List<UserPushTokenDTO> findNearbyUsers(double lat, double lng) {
        logger.debug("Started finding nearby users. Lat: {}, Lng: {}", lat, lng);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserPushTokenDTO> query = cb.createQuery(UserPushTokenDTO.class);
        Root<User> user = query.from(User.class);

        Predicate withinDistance = cb.isTrue(cb.function("ST_DWithin", Boolean.class,
                user.get("location"), cb.function("ST_SetSRID", Object.class,
                        cb.function("ST_MakePoint", Object.class, cb.literal(lng), cb.literal(lat)), cb.literal(4326)),
                cb.literal(searchRadiusKm)
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

    public ResponseEntity<EventDetailsDTO> getEvent(UUID id) {
        Optional<Event> eventOpt = eventRepository.findById(id);

        if (eventOpt.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Message", "No event found").body(null);
        }
        Event event = eventOpt.get();
        EventDetailsDTO eventDetailsDTO = getEventDetailsDTOfromEvent(event);


        return ResponseEntity.status(HttpStatus.OK).header("Message", "Found event").body(eventDetailsDTO);
    }

    public ResponseEntity<List<EventDetailsDTO>> getNearEvents(double lat, double longitude) {
        List<EventDetailsDTO> eventDetailsDTOS = findNearbyEvents(lat, longitude);

        if (eventDetailsDTOS.isEmpty()){
            logger.info("No events found");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(eventDetailsDTOS);
        }
        logger.info("Events :{} ", eventDetailsDTOS.toString());
        return ResponseEntity.status(HttpStatus.OK).body(eventDetailsDTOS);
    }

    public List<EventDetailsDTO> findNearbyEvents(double lat, double lng) {
        logger.debug("Started finding nearby events. Lat: {}, Lng: {}", lat, lng);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetailsDTO> query = cb.createQuery(EventDetailsDTO.class);
        Root<Event> event = query.from(Event.class);
        Join<Event, User> user = event.join("user");

        Predicate withinDistance = cb.isTrue(cb.function("ST_DWithin", Boolean.class,
                event.get("location"),
                cb.function("ST_SetSRID", Object.class,
                        cb.function("ST_MakePoint", Object.class, cb.literal(lng), cb.literal(lat)),
                        cb.literal(4326)),
                cb.literal(searchRadiusKm)
        ));

        Expression<Double> latitude = cb.function("ST_Y", Double.class, event.get("location"));
        Expression<Double> longitude = cb.function("ST_X", Double.class, event.get("location"));

        Predicate statusActive = cb.equal(event.get("currentStatus"), "ACTIVE");

        Predicate combined = cb.and(withinDistance, statusActive);

//        query.select(cb.construct(EventDetailsDTO.class,
//                user,
//                event.get("eid"),
//                event.get("bloodGroup"),
//                latitude,
//                longitude,
//                event.get("place"),
//                event.get("currentStatus")
//        )).where(withinDistance);

        query.select(cb.construct(EventDetailsDTO.class,
                user,
                event.get("eid"),
                event.get("bloodGroup"),
                latitude,
                longitude,
                event.get("place"),
                event.get("currentStatus")
        )).where(combined);
        logger.info("Query constructed for nearby events with location filter.");

        return entityManager.createQuery(query).getResultList();
    }

    public ResponseEntity<List<EventDetailsDTO>> getMyEvents(UUID id) {
        List<Event> events = eventRepository.findAllByUserUid(id);

        List<EventDetailsDTO> eventDtoList = new ArrayList<>();
        for (Event event : events) {
            eventDtoList.add(getEventDetailsDTOfromEvent(event));
        }

        return ResponseEntity.status(HttpStatus.OK).body(eventDtoList);

    }

    public ResponseEntity<List<EventDetailsDTO>> getMyActiveEvents(UUID id) {
        List<Event> events = eventRepository.findAllByUserUidAndCurrentStatus(id, Constants.EVENT_STATUS_CREATED);

        List<EventDetailsDTO> eventDtoList = new ArrayList<>();
        for (Event event : events) {
            eventDtoList.add(getEventDetailsDTOfromEvent(event));
        }

        return ResponseEntity.status(HttpStatus.OK).body(eventDtoList);

    }

    public ResponseEntity<EventDetailsDTO> updateEvent(UUID eid, EventDetailsDTO dto) {
        Event event = eventRepository.findById(eid)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (dto.getBloodGroup() != null) event.setBloodGroup(dto.getBloodGroup());
        if (dto.getCurrentStatus() != null) event.setCurrentStatus(dto.getCurrentStatus());
        if (dto.getPlace() != null) event.setPlace(dto.getPlace());

        if (dto.getLatitude() != 0.0 && dto.getLongitude() != 0.0) {
            Point location = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude())); // (lon, lat)
            location.setSRID(4326);
            event.setLocation(location);
        }

        eventRepository.save(event);

        EventDetailsDTO updatedDTO = getEventDetailsDTOfromEvent(event);
        return ResponseEntity.status(HttpStatus.OK)
                .header("message", "Event updated successfully")
                .body(updatedDTO);

    }
}
