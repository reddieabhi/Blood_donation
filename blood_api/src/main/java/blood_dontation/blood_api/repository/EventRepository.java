package blood_dontation.blood_api.repository;

import blood_dontation.blood_api.model.DTO.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository <Event, UUID>{
}
