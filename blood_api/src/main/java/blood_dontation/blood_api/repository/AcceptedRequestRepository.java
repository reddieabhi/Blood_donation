package blood_dontation.blood_api.repository;

import blood_dontation.blood_api.model.AcceptedRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;


public interface AcceptedRequestRepository extends JpaRepository <AcceptedRequest, UUID> {
    List<AcceptedRequest> findByEventId(UUID eid);
}

