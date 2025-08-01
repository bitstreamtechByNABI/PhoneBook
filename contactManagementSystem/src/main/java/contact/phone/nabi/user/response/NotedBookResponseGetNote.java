package contact.phone.nabi.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotedBookResponseGetNote {
	
	private Object data;
	private String message;
	private int statusCode;
	private String status;

}
