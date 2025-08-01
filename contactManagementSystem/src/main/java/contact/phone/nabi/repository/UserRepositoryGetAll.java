package contact.phone.nabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import contact.phone.nabi.user.model.*;

public interface UserRepositoryGetAll extends JpaRepository<User, Long> {
	
	boolean existsByUserId(String userId);

}
