package contact.phone.nabi.user.model.Notebook;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotebookEntryRequest {

	@NotBlank
	private String userId;
	private String noteStatus;
	@NotBlank
	private String noteBookName;
	private String noteBookContent;
	private String attachmentBase64;
	private String attachmentFileName;
	private String attachmentFileType;

}
