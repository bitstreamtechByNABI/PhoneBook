package contact.phone.nabi.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
	
	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
}
