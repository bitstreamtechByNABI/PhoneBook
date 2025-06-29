package contact.phone.nabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import contact.phone.nabi.user.model.OtpLog;

public interface OtpLogRepo extends JpaRepository<OtpLog, Long> {

	
    List<OtpLog> findByUserId(String userId);

    // Optional: Find latest OTP log for a specific email
    OtpLog findTopByEmailOrderByCreatedAtDesc(String email);
    
    @Query(value = "SELECT otp FROM otp_requests WHERE email = :email ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
	String getotpByEmail(@Param("email") String email);

	
}
