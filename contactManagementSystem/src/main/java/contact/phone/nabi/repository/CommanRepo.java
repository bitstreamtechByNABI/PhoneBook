package contact.phone.nabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import contact.phone.nabi.user.model.PhonebookUser;
import jakarta.transaction.Transactional;



public interface CommanRepo extends JpaRepository<PhonebookUser, Long> {
	
	@Query("SELECT u.email, u.firstName, u.lastName, u.phoneNumber, u.profileImage " +
		       "FROM PhonebookUser u " +
		       "WHERE u.userId = :userId AND u.contactActive = true")
		List<Object[]> findUserDetailsByUserId(@Param("userId") String userId);


		
		@Modifying
		@Transactional
		@Query(value = "UPDATE phonebook SET contact_active = false WHERE phone_number = :phoneNumber", nativeQuery = true)
		int findByPhoneNumber(@Param("phoneNumber") String phoneNumber);



		@Modifying
		@Query(value = "UPDATE phonebook " +
		               "SET first_name = :firstName, " +
		               "last_name = :lastName, " +
		               "email = :email, " +
		               "profile_image = :imageBytes, " +
		               "updated_date = NOW(), " +
		               "contact_active = 1 " +
		               "WHERE phone_number = :phoneNumber", 
		       nativeQuery = true)
		int updateContactDetails(@Param("phoneNumber") String phoneNumber,
		                         @Param("firstName") String firstName,
		                         @Param("lastName") String lastName,
		                         @Param("email") String email,
		                         @Param("imageBytes") byte[] imageBytes);










}
