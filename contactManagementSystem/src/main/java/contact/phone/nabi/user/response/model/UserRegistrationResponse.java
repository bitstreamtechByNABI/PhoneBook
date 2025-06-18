package contact.phone.nabi.user.response.model;

import java.util.List;

import contact.phone.nabi.user.model.UserResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationResponse {
	
	   private List<UserResult> result;
	    private String exceptionOccured;
	    private String exceptionMessage;
	    private int status;
	    private String message;



}
