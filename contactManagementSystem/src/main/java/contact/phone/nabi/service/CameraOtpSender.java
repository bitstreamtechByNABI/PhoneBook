package contact.phone.nabi.service;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.sarxos.webcam.Webcam;

import contact.phone.nabi.repository.OtpLogRepo;
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
	
	@Autowired
	private OtpLogRepo otpLogRepo;
	
	 public void capturePhotoAndSendOtp(String email) {
//		    String otp = otpService.generateOtp(email);
		    String otp = otpLogRepo.getotpByEmail(email);
		    System.out.println("OTP : - "+otp);
		 
	        try {
	            // Open camera
	            Webcam webcam = Webcam.getDefault();
	            Dimension[] sizes = webcam.getViewSizes();
	            webcam.setViewSize(new Dimension(640, 480));
	            webcam.open();

	            // Capture image
	            BufferedImage image = webcam.getImage();
	            File photoFile = new File("otp_photo.jpg");
	            ImageIO.write(image, "JPG", photoFile);
	            webcam.close();
	            emailService.sendOtpEmail(email, otp, photoFile);

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

}
