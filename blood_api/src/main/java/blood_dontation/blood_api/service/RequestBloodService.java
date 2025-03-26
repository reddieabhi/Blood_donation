package blood_dontation.blood_api.service;

import blood_dontation.blood_api.constants.Constants;
import blood_dontation.blood_api.model.DTO.Event;
import blood_dontation.blood_api.model.DTO.User;
import blood_dontation.blood_api.repository.EventRepository;
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
    private EventRepository eventRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public RequestBloodService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public List<User> handleBloodRequest(double lat, double lng, String bloodGroup, UUID userId) {

        List<User> nearbyUsers = findNearbyUsers(lat, lng, bloodGroup);

        Point location = geometryFactory.createPoint(new Coordinate(lng, lat));

        Optional<User> userOpt = entityManager.find(User.class, userId) != null
                ? Optional.of(entityManager.find(User.class, userId))
                : Optional.empty();

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found!");
        }

        Event event = new Event();
        event.setUser(userOpt.get());
        event.setBloodGroup(bloodGroup);
        event.setLocation(location);
        event.setCurrentStatus(Constants.EVENT_STATUS_CREATED);
        eventRepository.save(event);

        return nearbyUsers;
    }

    public List<User> findNearbyUsers(double lat, double lng, String bloodGroup) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> user = query.from(User.class);


        Predicate withinDistance = cb.isTrue(cb.function("ST_DWithin", Boolean.class,
                user.get("location"), cb.function("ST_SetSRID", Object.class,
                        cb.function("ST_MakePoint", Object.class, cb.literal(lng), cb.literal(lat)), cb.literal(4326)),
                cb.literal(searchRadiusKm * 1000)
        ));

        query.select(user).where(withinDistance);
        return entityManager.createQuery(query).getResultList();
    }
}
