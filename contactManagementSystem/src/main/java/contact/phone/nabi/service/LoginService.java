package contact.phone.nabi.service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import contact.phone.nabi.repository.UserRepository;
import contact.phone.nabi.service.impl.UserServiceImpl;
import contact.phone.nabi.user.model.LoginRequest;
import contact.phone.nabi.user.model.User;
import contact.phone.nabi.user.model.component.OtpHandler;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class LoginService {


    @Autowired
    private OtpHandler otpHandler;
    @Autowired
    private  CameraOtpSender cameraOtpSender;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    public contact.phone.nabi.user.model.OtpLoginResponse loginWithOtp(LoginRequest request,MultipartFile photo ,HttpServletRequest httpRequest) {
        contact.phone.nabi.user.model.OtpLoginResponse response = new contact.phone.nabi.user.model.OtpLoginResponse();
        response.setExceptionOccured("");
        response.setExceptionMessage("");
        response.setMessage("");

        String username = request.getUsername();
        String password = request.getPassword();

        if (!userRepository.existsByUserName(username)) {
            response.setStatus(otpHandler.getFailureStatus());
            response.setResult(Collections.singletonList(Map.of("otpStatus", "Username does not exist")));
            return response;
        }

        if (!userService.isUserValid(username, password)) {
            response.setStatus(0);
            response.setResult(Collections.singletonList(Map.of("otpStatus", "Invalid username or password")));
            return response;
        }

        Boolean userStatus = userRepository.existsByUserName(username);
        if (userStatus == null || !userStatus) {
            response.setStatus(otpHandler.getFailureStatus());
            response.setResult(Collections.singletonList(Map.of("otpStatus", "User is inactive or does not exist")));
            return response;
        }

        String email = userService.getEmailByUsername(username);
        if (email == null || email.isEmpty()) {
            response.setStatus(otpHandler.getFailureStatus());
            response.setResult(Collections.singletonList(Map.of("otpStatus", "Email not registered")));
            return response;
        }

        // Generate OTP
        String otp = otpService.generateOtp(email);
        
        
        cameraOtpSender.capturePhotoAndSendOtp(email,otp);

        // Handle photo file
        File imageFile = null;
        if (photo != null && !photo.isEmpty()) {
            try {
                imageFile = File.createTempFile("username :", ".jpg");
                photo.transferTo(imageFile); // Save to disk
            } catch (IOException e) {
                e.printStackTrace();
            }
       }

        User user = userRepository.findByUserName(username);
        String userId = user != null ? String.valueOf(user.getUserId()) : "UNKNOWN";
        otpService.logOtpEvent(userId, username, email, otp, httpRequest);

        Map<String, String> resultData = new HashMap<>();
        resultData.put("otpStatus", "OTP sent to registered email");
        resultData.put("email", email);
        response.setStatus(otpHandler.getSuccessStatus());
        response.setResult(Collections.singletonList(resultData));
        
        return response;
    }
}
