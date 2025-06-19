package contact.phone.nabi.user.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class FinalResponse {
	
	private String userId;
    private List<UserResponse> contactList;

}
