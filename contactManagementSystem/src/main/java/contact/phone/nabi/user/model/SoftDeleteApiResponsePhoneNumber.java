package contact.phone.nabi.user.model;

import lombok.Data;

@Data
public class SoftDeleteApiResponsePhoneNumber {

	private Object data;
	private String flag1;
	private String flag2;
	private int statusCode;
	private String message;

	public SoftDeleteApiResponsePhoneNumber(Object data, String flag1, String flag2, int statusCode, String message) {
		this.data = data;
		this.flag1 = flag1;
		this.flag2 = flag2;
		this.statusCode = statusCode;
		this.message = message;
	}
}
