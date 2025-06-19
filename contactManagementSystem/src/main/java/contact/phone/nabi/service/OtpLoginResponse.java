package contact.phone.nabi.service;

import contact.phone.nabi.user.model.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface OtpLoginResponse {
	
	OtpLoginResponse loginWithOtp(LoginRequest request, HttpServletRequest httpRequest);


}
