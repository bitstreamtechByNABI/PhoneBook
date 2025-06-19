package contact.phone.nabi.emailt.emplate;

public class EmailTemplateBuilder {
	
	 public static String buildLoginEmail(String name, String ip, String location, boolean isSuccess) {
	        String title = isSuccess ? "✅ Login Successful" : "❌ Login Failed";
	        String message = isSuccess ? "You have successfully logged in." : "An unsuccessful login attempt was made.";

	        return "<html><body style='font-family: Arial, sans-serif;'>"
	            + "<h2 style='color: #2c6ca0;'>" + title + "</h2>"
	            + "<p>Hi <strong>" + name + "</strong>,</p>"
	            + "<p>" + message + "</p>"
	            + "<table style='border: 1px solid #ccc; padding: 10px;'>"
	            + "<tr><td><strong>IP Address:</strong></td><td>" + ip + "</td></tr>"
	            + "<tr><td><strong>Location:</strong></td><td>" + location + "</td></tr>"
	            + "</table><br/>"
	            + "<p style='color: red;'>⚠️ Please do not share your OTP with anyone.</p>"
	            + "<p>If this wasn't you, please secure your account immediately.</p>"
	            + "<hr><small style='color:#888;'>Finagg Security System</small>"
	            + "</body></html>";
	    }

}
