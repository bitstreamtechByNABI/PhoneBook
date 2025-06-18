package contact.phone.nabi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import contact.phone.nabi.user.model.OtpLog;

public interface OtpLogRepo extends JpaRepository<OtpLog, Long> {

	
    List<OtpLog> findByUserId(String userId);

    // Optional: Find latest OTP log for a specific email
    OtpLog findTopByEmailOrderByCreatedAtDesc(String email);
}
