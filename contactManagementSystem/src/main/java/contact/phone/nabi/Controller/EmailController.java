package contact.phone.nabi.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import contact.phone.nabi.service.impl.NotesEmailService;
import contact.phone.nabi.user.model.ApiResponseEmail;



@RestController
@RequestMapping("/api/email")
public class EmailController {
	
	 @Autowired
	 private NotesEmailService emailService;
	
	 @PostMapping("/send")
	 public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> request) {
	     try {
	         String to = request.get("to");
	         String subject = request.get("subject");
	         String body = request.get("body");

	         // Validate input fields
	         if (to == null || to.isEmpty() || !to.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
	             return ResponseEntity
	                     .badRequest()
	                     .body(new ApiResponseEmail("❌ Invalid email address: " + to, 400)); // 400 Bad Request: Invalid email format
	         }

	         if (subject == null || subject.isEmpty()) {
	             return ResponseEntity
	                     .badRequest()
	                     .body(new ApiResponseEmail("❌ Email subject cannot be empty.", 400)); // 400 Bad Request: Missing subject
	         }

	         if (body == null || body.isEmpty()) {
	             return ResponseEntity
	                     .badRequest()
	                     .body(new ApiResponseEmail("❌ Email body cannot be empty.", 400)); // 400 Bad Request: Missing email body
	         }

	         // Send the email
	         emailService.sendEmail(to, subject, body);

	         // 200 OK: Email sent successfully
	         return ResponseEntity.ok(new ApiResponseEmail("✅ Email sent successfully!", 200));

	     } catch (IllegalArgumentException e) {
	         // 400 Bad Request: Invalid email address format
	         return ResponseEntity
	                 .badRequest()
	                 .body(new ApiResponseEmail("❌ Invalid email address: " + e.getMessage(), 400));
	     } catch (MailException e) {
	         // 500 Internal Server Error: SMTP error
	         return ResponseEntity
	                 .status(500)
	                 .body(new ApiResponseEmail("❌ SMTP Error: " + e.getMessage(), 500));
	     } catch (Exception e) {
	         // 500 Internal Server Error: Catch-all for unexpected errors
	         return ResponseEntity
	                 .status(500)
	                 .body(new ApiResponseEmail("❌ Failed to send email: " + e.getMessage(), 500));
	     }
	 }

}
