package contact.phone.nabi.user.model.Notebook;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotebookListResponse {

	 private Object content;     
	    private int statusCode;
	    private String message;
}
