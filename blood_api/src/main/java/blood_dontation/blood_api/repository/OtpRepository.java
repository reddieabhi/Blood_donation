package blood_dontation.blood_api.repository;

import blood_dontation.blood_api.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<OtpVerification, UUID> {
    Optional<OtpVerification> findByEmailAndVerifiedFalse(String email);
}
