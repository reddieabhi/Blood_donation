package blood_dontation.blood_api.repository;

import blood_dontation.blood_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository <User, UUID> {

}
