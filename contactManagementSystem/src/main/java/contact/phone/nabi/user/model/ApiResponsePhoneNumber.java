package contact.phone.nabi.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponsePhoneNumber {
	private FinalResponse phoneNumberList;
	private String exceptionOccured;
	private String exceptionMessage;
	private int status;
	private String message;

}
