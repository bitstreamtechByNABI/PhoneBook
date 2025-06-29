package contact.phone.nabi.user.model.Notebook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotedBookResponse {

	private Object data;
	private int statusCode;
	private String message;
}
