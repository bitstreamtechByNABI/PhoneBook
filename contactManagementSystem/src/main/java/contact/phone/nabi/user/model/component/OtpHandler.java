package contact.phone.nabi.user.model.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component

public class OtpHandler {
	
	  @Value("${otp.login.success.status}")
	    private int otpLoginSuccessStatus;

	    @Value("${otp.login.failure.status}")
	    private int otpLoginFailureStatus;

	    public int getSuccessStatus() {
	        return otpLoginSuccessStatus;
	    }

	    public int getFailureStatus() {
	        return otpLoginFailureStatus;
	    }

}
