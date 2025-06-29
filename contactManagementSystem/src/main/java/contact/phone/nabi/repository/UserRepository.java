package contact.phone.nabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import contact.phone.nabi.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	

	boolean existsByEmail(String email);

	boolean existsByUserName(String userName); 

	boolean existsByPhone(String phone);

	boolean existsByUserId(String userId);
	
	User findByUserName(String userName);


	 @Query(value = "SELECT password FROM users WHERE user_name = :username", nativeQuery = true)
	    String getPasswordHash(@Param("username") String username);
	 
	 public User findByEmail(String email);

	 @Query(value = "SELECT user_status FROM users WHERE user_id = :userId", nativeQuery = true)
	 Boolean findUserStatusByUserId(@Param("userId") String userId);

		@Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM users WHERE user_name = :userName", nativeQuery = true)
		Boolean existsUserName(@Param("userName") String userName);

		@Query(value = "SELECT user_name FROM users WHERE email = :toEmail", nativeQuery = true)
		String getUserName(String toEmail);

		@Query(value = "SELECT otp FROM otp_logs WHERE email = :email ORDER BY created_on DESC LIMIT 1", nativeQuery = true)
		String getOtpByEmail(@Param("email") String email);
		
		
		@Query(value = "SELECT user_status FROM users WHERE user_id = :userId", nativeQuery = true)
		String findUserStatusByUserIdStr(@Param("userId") String userId);

		
		@Query(value = "SELECT user_status FROM users WHERE user_id = :userId", nativeQuery = true)
		Integer findUserStatusByUserIdInt(@Param("userId") String userId);

		
		

	




}
