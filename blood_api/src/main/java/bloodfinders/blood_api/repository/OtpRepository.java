package bloodfinders.blood_api.repository;

import bloodfinders.blood_api.model.OtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<OtpEntity, UUID> {
    Optional<OtpEntity> findByEmailAndVerifiedFalse(String email);

    OtpEntity findByEmail(String email);
}
