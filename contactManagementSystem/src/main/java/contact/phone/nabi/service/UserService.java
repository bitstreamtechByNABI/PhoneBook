package contact.phone.nabi.service;

import contact.phone.nabi.user.model.UserRegistrationRequest;
import contact.phone.nabi.user.response.model.UserRegistrationResponse;

public interface UserService {
	
	 UserRegistrationResponse registerUser(UserRegistrationRequest request);
	 boolean isUserValid(String username, String rawPassword);

	    String getEmailByUsername(String username);
	    

}
