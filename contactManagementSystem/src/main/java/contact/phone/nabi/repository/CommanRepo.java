package contact.phone.nabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import contact.phone.nabi.user.model.PhonebookUser;



public interface CommanRepo extends JpaRepository<PhonebookUser, Long> {
	
	 @Query("SELECT u.email, u.firstName, u.lastName, u.phoneNumber FROM PhonebookUser u WHERE u.userId = :userId")
	    List<Object[]> findUserDetailsByUserId(@Param("userId") String userId);

}
