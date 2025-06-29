package contact.phone.nabi.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import contact.phone.nabi.repository.OtpRequestRepository;
import contact.phone.nabi.repository.UserRepository;
import contact.phone.nabi.user.model.OtpLog;
import contact.phone.nabi.user.model.otpRequest.photo.OtpRequestEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailService {


	@Autowired
	private UserRepository userReoRepository;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private OtpRequestRepository otpRequestRepository;

   

	 public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
	 
	   public void sendHtmlEmailWithPdf(String to, String subject, String htmlBody, byte[] pdfBytes, String pdfFileName) {
	        try {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true); 
	            helper.setTo(to);
	            helper.setSubject(subject);
	            helper.setText(htmlBody, true);

	            helper.addAttachment(pdfFileName, new ByteArrayDataSource(pdfBytes, "application/pdf"));

	            mailSender.send(message);
	        } catch (Exception e) {
	            e.printStackTrace(); 
	        }
	    }
	   
	   public void sendOtpWithImage(String toEmail, String otp, File imageFile) {
		    try {
		        MimeMessage message = mailSender.createMimeMessage();
		        MimeMessageHelper helper = new MimeMessageHelper(message, true);

		        helper.setTo(toEmail);
		        helper.setSubject("Your OTP and Captured Photo");
		        helper.setText("Dear user,\n\nYour OTP is: " + otp + "\n\nThanks.");

		        if (imageFile != null && imageFile.exists()) {
		            FileSystemResource file = new FileSystemResource(imageFile);
		            helper.addAttachment("photo.jpg", file);
		        }

		        mailSender.send(message);
		    } catch (MessagingException e) {
		        e.printStackTrace();
		    }

		}
	
//	   public void sendOtpEmail(String toEmail, String otp, File photoFile) {
//		    try {
//		        MimeMessage message = mailSender.createMimeMessage();
//		        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//		        helper.setTo(toEmail);
//		        helper.setSubject("Your OTP for Verification – PhonebookNotes");
//		        
//		        String userName = userReoRepository.getUserName(toEmail);
//
//		        String emailBody = "<p>Dear "+userName+"</p>"
//		                + "<p>Thank you for choosing <strong>PhonebookNotes</strong>.</p>"
//		                + "<p>To proceed with your verification, please use the One-Time Password (OTP) provided below:</p>"
//		                + "<p style=\"font-size:18px;\"><strong>🔐 OTP: " + otp + "</strong></p>"
//		                + "<p style=\"color: #FF0000;\">Please do not share this OTP with anyone. This code will expire in 10 minutes.</p>"
//		                + "<p>If you did not request this verification, please ignore this email or contact our support team immediately.</p>"
//		                + "<br/>"
//		                + "<p>Best regards,<br/>PhonebookNotes Team</p>";
//
//		        helper.setText(emailBody, true); // Enable HTML content
//
//		        if (photoFile != null && photoFile.exists()) {
//		            helper.addAttachment("otp_photo.jpg", photoFile);
//		        }
//
//		        mailSender.send(message);
//		        System.out.println("OTP email sent. " + otp);
//		    } catch (MessagingException e) {
//		        e.printStackTrace();
//		    }
//		}

	   
	   public void sendOtpEmail(String toEmail, String otp, File photoFile) {
		    try {
		        // Step 1: Send Email (Same as before)
		        MimeMessage message = mailSender.createMimeMessage();
		        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		        helper.setTo(toEmail);
		        helper.setSubject("Your OTP for Verification – PhonebookNotes");

		        String emailBody = "<p>Dear User,</p>"
		                + "<p>Thank you for choosing <strong>PhonebookNotes</strong>.</p>"
		                + "<p>To proceed with your verification, please use the One-Time Password (OTP) provided below:</p>"
		                + "<p style=\"font-size:18px;\"><strong>🔐 OTP: " + otp + "</strong></p>"
		                + "<p><em>(This is a sample – replace with your actual generated OTP)</em></p>"
		                + "<p style=\"color: #FF0000;\">Please do not share this OTP with anyone. This code will expire in 10 minutes.</p>"
		                + "<p>If you did not request this verification, please ignore this email or contact our support team immediately.</p>"
		                + "<br/>"
		                + "<p>Best regards,<br/>PhonebookNotes Team</p>";

		        helper.setText(emailBody, true);

		        if (photoFile != null && photoFile.exists()) {
		            helper.addAttachment("otp_photo.jpg", photoFile);
		        }

		        mailSender.send(message);
		        System.out.println("OTP email sent. " + otp);

		        // Step 2: Save to DB
		        OtpRequestEntity entity = new OtpRequestEntity();
		        entity.setEmail(toEmail);
		        entity.setOtp(otp); 
		        entity.setCreatedAt(LocalDateTime.now());

		        if (photoFile != null && photoFile.exists()) {
		            entity.setPhoto(Files.readAllBytes(photoFile.toPath()));
		        }

		        otpRequestRepository.save(entity);  

		    } catch (MessagingException | IOException e) {
		        e.printStackTrace();
		    }

	   }
	}
