package contact.phone.nabi.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PhoneBookRequest {
	
	   @NotBlank(message = "userId is mandatory")
	    @Size(max = 20, message = "userId must be at most 20 characters")
	    private String userId;

	    @NotBlank(message = "firstName is mandatory")
	    @Size(max = 50, message = "firstName must be at most 50 characters")
	    private String firstName;

	    @NotBlank(message = "lastName is mandatory")
	    @Size(max = 50, message = "lastName must be at most 50 characters")
	    private String lastName;

	 
	    @Email(message = "Invalid email format")
	    @Size(max = 100, message = "email must be at most 100 characters")
	    private String email;

	    @NotBlank(message = "phoneNumber is mandatory")
	    @Size(max = 15, message = "phoneNumber must be at most 15 characters")
	    private String phoneNumber;

}
