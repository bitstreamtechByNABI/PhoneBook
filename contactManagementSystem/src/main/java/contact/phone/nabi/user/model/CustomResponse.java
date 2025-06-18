package contact.phone.nabi.user.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomResponse {

	 private List<Result> result;
	    private String exceptionOccured;
	    private String exceptionMessage;
	    private int status;
	    private String message;
}
