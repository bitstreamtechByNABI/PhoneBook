package contact.phone.nabi.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import contact.phone.nabi.user.model.otpRequest.photo.OtpRequestEntity;
import jakarta.transaction.Transactional;

public interface OtpRequestRepository extends JpaRepository<OtpRequestEntity, Long> {

	

	@Modifying
	@Transactional
	@Query(value = "UPDATE otp_logs SET otp = :otp, created_at = :createdAt WHERE email = :email", nativeQuery = true)
	int updateOtpByEmail(@Param("email") String email, @Param("otp") String otp, @Param("createdAt") LocalDateTime createdAt);

	
	

}
