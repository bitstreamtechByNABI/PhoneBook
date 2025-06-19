package contact.phone.nabi.service;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.sarxos.webcam.Webcam;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor

@Service
public class CameraOtpSender {
	
	@Autowired
	 private final EmailService emailService;
	
	@Autowired
	 private final OtpService otpService;
	
	 public void capturePhotoAndSendOtp(String email) {
		    String otp = otpService.generateOtp(email); 
		 
	        try {
	            // Open camera
	            Webcam webcam = Webcam.getDefault();
	            Dimension[] sizes = webcam.getViewSizes();
	            for (Dimension size : sizes) {
	                System.out.println("Supported size: " + size);
	            }
	            webcam.setViewSize(new Dimension(640, 480));
	            webcam.open();

	            // Capture image
	            BufferedImage image = webcam.getImage();
	            File photoFile = new File("otp_photo.jpg");
	            ImageIO.write(image, "JPG", photoFile);
	            webcam.close();

	            // Send email with photo
	            emailService.sendOtpEmail(email, otp, photoFile);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

}
