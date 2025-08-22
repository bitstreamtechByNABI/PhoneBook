package contact.phone.nabi.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class NotesEmailService {
	private static final Logger logger = LoggerFactory.getLogger(NotesEmailService.class);

	@Autowired
	private JavaMailSender mailSender;

	 /**
     * Send email without attachment
     */
	public void sendEmail(String to, String subject, String body) {
	    try {
	        validateEmail(to);

	        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	        String greeting = getGreetingMessage();
	        String emailBody = buildEmailBody(body, currentDateTime, greeting);

	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true); // HTML enabled
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(emailBody, true);

	        mailSender.send(message);
	        logger.info("Email sent successfully to: {}", to);

	    } catch (Exception e) {
	        logger.error("Failed to send email to: {}", to, e);
	        throw new RuntimeException("Failed to send email.", e);
	    }
	}


    /**
     * Send email with optional attachment
     */
    public void sendEmailWithAttachment(String to, String subject, String body, MultipartFile attachment) {
        try {
            validateEmail(to);

            String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String greeting = getGreetingMessage();
            String emailBody = buildEmailBody(body, currentDateTime, greeting);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true); // multipart = true
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(emailBody, true); // HTML

            // Attach file if present
            if (attachment != null && !attachment.isEmpty()) {
                InputStreamSource source = new ByteArrayResource(attachment.getBytes());
                helper.addAttachment(attachment.getOriginalFilename(), source);
            }

            mailSender.send(message);
            logger.info("Email with attachment sent successfully to: {}", to);

        } catch (IOException | MessagingException | MailException e) {
            logger.error("Error sending email to: {}", to, e);
            throw new RuntimeException("Failed to send email.", e);
        }
    }

    /**
     * Validate basic email format
     */
    private void validateEmail(String to) {
        if (to == null || !to.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email address: " + to);
        }
    }

    /**
     * Returns a greeting based on current time
     */
    private String getGreetingMessage() {
        int hour = LocalDateTime.now().getHour();
        if (hour >= 5 && hour < 12) return "Good Morning";
        if (hour >= 12 && hour < 18) return "Good Afternoon";
        return "Good Evening";
    }

    /**
     * Builds HTML email body
     */
    private String buildEmailBody(String originalBody, String currentDateTime, String greetingMessage) {
        return String.format(
                "<html><body style='font-family: Arial, sans-serif;'>"
                        + "<p>Hello,</p>"
                        + "<p><b>%s,</b></p>"
                        + "<p>This is a personalized email sent at <span style='color:red;'>%s</span>.</p>"
                        + "<p>%s</p>"
                        + "<hr>"
                        + "<footer>"
                        + "<p><b>Kind Regards,</b><br>📑 phonebooknotes 📑</p>"
                        + "</footer>"
                        + "</body></html>",
                greetingMessage, currentDateTime, originalBody
        );
    }
}
