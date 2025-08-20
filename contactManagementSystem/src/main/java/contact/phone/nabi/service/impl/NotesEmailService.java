package contact.phone.nabi.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class NotesEmailService {
	private static final Logger logger = LoggerFactory.getLogger(NotesEmailService.class);

	@Autowired
	private JavaMailSender mailSender;

	 public void sendEmail(String to, String subject, String body) {
	        try {
	            // Validate email address (basic validation)
	            if (to == null || !to.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
	                throw new IllegalArgumentException("Invalid email address: " + to);
	            }

	            // Add dynamic data to the email body
	            String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	            String userGreeting = getGreetingMessage();
	            String emailBody = buildEmailBody(body, currentDateTime, userGreeting);

	            // Create a MimeMessage for sending HTML content
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);  
	            helper.setTo(to);
	            helper.setSubject(subject);
	            helper.setText(emailBody, true);
	            mailSender.send(message);

	            logger.info("Email sent successfully to: {}", to);

	        } catch (IllegalArgumentException e) {
	            logger.error("Invalid email address provided: {}", to);
	            throw new RuntimeException("Invalid email address provided", e);
	        } catch (MailException e) {
	            logger.error("Error sending email to: {}", to, e);
	            throw new RuntimeException("Failed to send email due to SMTP error.", e);
	        } catch (MessagingException e) {
	            logger.error("Error creating email message to: {}", to, e);
	            throw new RuntimeException("Failed to create email message.", e);
	        } catch (Exception e) {
	            logger.error("Unexpected error while sending email to: {}", to, e);
	            throw new RuntimeException("An unexpected error occurred while sending the email.", e);
	        }
	    }

	    private String getGreetingMessage() {
	        int hour = LocalDateTime.now().getHour();

	        if (hour >= 5 && hour < 12) {
	            return "Good Morning";
	        } else if (hour >= 12 && hour < 18) {
	            return "Good Afternoon";
	        } else {
	            return "Good Evening";
	        }
	    }

	    private String buildEmailBody(String originalBody, String currentDateTime, String greetingMessage) {
	        return String.format(
	                "<html><body style='font-family: Arial, sans-serif;'>" +
	                        "<p>Hello,</p>" +
	                        "<p><b>%s,</b></p>" +
	                        "<p>This is a personalized email sent at <span style='color:red;'>%s</span>.</p>" +
	                        "<p>%s</p>" +
	                        "<hr>" +
	                        "<footer>" +
	                        "<p><b>Kind Regards,</b><br>📑 phonebooknotes 📑</p>" +
	                        "</footer>" +
	                        "</body></html>",
	                greetingMessage, currentDateTime, originalBody
	        );
	    }

}
