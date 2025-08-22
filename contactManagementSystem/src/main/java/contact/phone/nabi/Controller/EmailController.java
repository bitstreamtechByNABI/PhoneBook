package contact.phone.nabi.Controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import contact.phone.nabi.service.impl.NotesEmailService;
import contact.phone.nabi.user.model.ApiResponseEmail;



@RestController
@RequestMapping("/api/email")
public class EmailController {
	
	 @Autowired
	 private NotesEmailService emailService;
	 
	
	 @PostMapping(value = "/sendWithAttachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	 public ResponseEntity<?> sendEmailWithAttachment(
	         @RequestPart("to") String to,
	         @RequestPart("subject") String subject,
	         @RequestPart("body") String body,
	         @RequestPart(value = "attachment", required = false) MultipartFile attachment) {

	     try {
	         // Input validation
	         if (to == null || to.isEmpty() || !to.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
	             return ResponseEntity.badRequest().body(new ApiResponseEmail("❌ Invalid email address: " + to, 400));
	         }
	         if (subject == null || subject.isEmpty()) {
	             return ResponseEntity.badRequest().body(new ApiResponseEmail("❌ Email subject cannot be empty.", 400));
	         }
	         if (body == null || body.isEmpty()) {
	             return ResponseEntity.badRequest().body(new ApiResponseEmail("❌ Email body cannot be empty.", 400));
	         }

	         // Send email
	         if (attachment != null && !attachment.isEmpty()) {
	             emailService.sendEmailWithAttachment(to, subject, body, attachment);
	         } else {
	             emailService.sendEmail(to, subject, body);
	         }

	         return ResponseEntity.ok(new ApiResponseEmail("✅ Email sent successfully!", 200));

	     } catch (Exception e) {
	         return ResponseEntity.status(500).body(new ApiResponseEmail("❌ Failed to send email: " + e.getMessage(), 500));
	     }
	 }

}
