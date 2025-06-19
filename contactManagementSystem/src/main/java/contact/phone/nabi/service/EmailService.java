package contact.phone.nabi.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailService {
	
	@Autowired
    private JavaMailSender mailSender;

//    public void sendOtpEmail(String toEmail, String otp, File photoFile) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        System.out.println("this is otp : "+otp);
//        message.setTo(toEmail);
//        message.setSubject("Your OTP Code");
//        message.setText("Your OTP is: " + otp);
//        mailSender.send(message);
//    }

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
	   
	   
	   public void sendOtpEmail(String toEmail, String otp, File photoFile) {
		    try {
		        MimeMessage message = mailSender.createMimeMessage();
		        MimeMessageHelper helper = new MimeMessageHelper(message, true);
		        helper.setTo(toEmail);
		        helper.setSubject("Your OTP Code with Photo");
		        helper.setText("Your OTP is: " + otp);

		        if (photoFile != null && photoFile.exists()) {
		            helper.addAttachment("otp_photo.jpg", photoFile);
		        }

		        mailSender.send(message);
		        System.out.println("OTP email sent. " +otp);
		    } catch (MessagingException e) {
		        e.printStackTrace();
		    }
		}


	}
