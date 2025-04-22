package bloodfinders.blood_api.repository;

import bloodfinders.blood_api.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository <Event, UUID>{
    List<Event> findByEid(UUID eid);
}
