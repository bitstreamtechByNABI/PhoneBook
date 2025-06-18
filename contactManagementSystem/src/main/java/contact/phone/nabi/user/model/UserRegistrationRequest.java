package contact.phone.nabi.user.model;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

//UserRegistration class to store user information
public class UserRegistrationRequest {

	
	private String firstName;
	 private String userName;
	private String lastName;
	private String email;
	private String password;
	private String phone;
	private String dob;
	private String gender;
	private Address address;
	private ArrayList<SecurityQuestion> securityQuestions;

}
