package contact.phone.nabi.user.model.Notebook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotebookResponseDTO {

	private String userId;
	private String noteStatus;
	private String noteBookName;
	private String noteBookContent;
}
