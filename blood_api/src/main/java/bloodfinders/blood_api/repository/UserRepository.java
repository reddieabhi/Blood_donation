package bloodfinders.blood_api.repository;

import bloodfinders.blood_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository <User, UUID> {

}
