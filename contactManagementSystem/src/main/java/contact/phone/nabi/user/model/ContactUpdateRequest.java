package contact.phone.nabi.user.model;

import lombok.Data;

@Data
public class ContactUpdateRequest {

	private String firstName;
	private String lastName;
	private String email;
	private String image;
}
