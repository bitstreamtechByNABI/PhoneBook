package contact.phone.nabi.user.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "otp_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpLog {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	private String userId;
	private String username;
	private String email;
	private String otp;
	private String ipAddress;
	private String macAddress;
	private String deviceName;
	private LocalDateTime createdAt;
}
