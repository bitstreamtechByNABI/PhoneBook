package contact.phone.nabi.user.model;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpLoginResponse {

	private List<Map<String, String>> result;
    private String exceptionOccured;
    private String message;
    private String exceptionMessage;
    private int status;
}
