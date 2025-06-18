package contact.phone.nabi.user.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class SecurityQuestion {

	   private String question;
	   private String answer;
}
