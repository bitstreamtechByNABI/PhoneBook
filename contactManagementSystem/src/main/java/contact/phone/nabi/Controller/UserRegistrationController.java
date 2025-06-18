package contact.phone.nabi.Controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import contact.phone.nabi.repository.UserRepository;
import contact.phone.nabi.service.EmailService;
import contact.phone.nabi.service.OtpService;
import contact.phone.nabi.service.impl.UserServiceImpl;
import contact.phone.nabi.user.model.CustomResponse;
import contact.phone.nabi.user.model.LoginRequest;
import contact.phone.nabi.user.model.OtpLoginResponse;
import contact.phone.nabi.user.model.OtpRequest;
import contact.phone.nabi.user.model.PhoneBookRequest;
import contact.phone.nabi.user.model.PhonebookUser;
import contact.phone.nabi.user.model.Result;
import contact.phone.nabi.user.model.User;
import contact.phone.nabi.user.model.UserRegistrationRequest;
import contact.phone.nabi.user.response.model.UserRegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserRegistrationController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class); // Replace with your actual controller class name


    @Autowired
    private UserServiceImpl userService;  
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private OtpService otpService;
    
    @Autowired
    private UserRepository userRepository;
 

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> registerUser(
            @RequestBody UserRegistrationRequest request) { 

        UserRegistrationResponse response = userService.registerUser(request);
        return ResponseEntity.ok(response);
    }
    
    
    @PostMapping("/loginUser")
    public ResponseEntity<OtpLoginResponse> login(@RequestBody LoginRequest request) {
        logger.info("Login attempt for username: {}", request.getUsername());
        System.out.println("UserName " + request.getUsername() + " password " + request.getPassword());

        OtpLoginResponse response = new OtpLoginResponse();
        response.setExceptionOccured("");
        response.setExceptionMessage("");
        response.setMessage("");

        if (!userService.isUserValid(request.getUsername(), request.getPassword())) {
            logger.info("Invalid login credentials for username: {}", request.getUsername());
            response.setStatus(0);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("otpStatus", "Invalid username or password");
            response.setResult(Collections.singletonList(resultMap));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String email = userService.getEmailByUsername(request.getUsername());
        if (email == null || email.isEmpty()) {
            logger.info("No registered email found for username: {}", request.getUsername());
            response.setStatus(0);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("otpStatus", "Email not registered");
            response.setResult(Collections.singletonList(resultMap));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);
        logger.info("OTP sent to registered email: {}", email);

        // Success response
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("otpStatus", "OTP sent to registered email");
        response.setResult(Collections.singletonList(resultMap));
        response.setStatus(1);
        return ResponseEntity.ok(response);
    }

	
	

    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody OtpRequest request) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> resultEntry = new HashMap<>();

        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (isValid) {
            User user = userRepository.findByEmail(request.getEmail());
            String fullName = user.getFirstName() + " " + user.getLastName();
            String userId = user.getUserId();

            resultEntry.put("message", userService.getGreetingBasedOnTime() + ", " + fullName);
            resultEntry.put("UserId :", userId);
            response.put("message", "Login successful");
            response.put("status", 1);
        } else {
            resultEntry.put("message", "Invalid or expired OTP");
            response.put("message", "Invalid or expired OTP");
            response.put("status", 0);
        }

        result.add(resultEntry);
        response.put("result", result);
        response.put("exceptionOccured", "");
        response.put("exceptionMessage", "");

        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/loginUserip")
    public ResponseEntity<OtpLoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        logger.info("Login attempt for username: {}", request.getUsername());

        OtpLoginResponse response = new OtpLoginResponse();
        response.setExceptionOccured("");
        response.setExceptionMessage("");
        response.setMessage("");

        if (!userService.isUserValid(request.getUsername(), request.getPassword())) {
            response.setStatus(0);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("otpStatus", "Invalid username or password");
            response.setResult(Collections.singletonList(resultMap));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String email = userService.getEmailByUsername(request.getUsername());
        if (email == null || email.isEmpty()) {
            response.setStatus(0);
            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("otpStatus", "Email not registered");
            response.setResult(Collections.singletonList(resultMap));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);

        // Save log
        User user = userRepository.findByUserName(request.getUsername());
        String userId = user != null ? String.valueOf(user.getId()) : "UNKNOWN";
        userService.logOtpEvent(userId, request.getUsername(), email, otp, httpRequest);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("otpStatus", "OTP sent to registered email");
        response.setResult(Collections.singletonList(resultMap));
        response.setStatus(1);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/phone/no/register")
    public ResponseEntity<?> phoneNoRegister(@Valid @RequestBody PhoneBookRequest request) {
        try {
            PhonebookUser savedUser = userService.saveUserIfActive(request);

            List<Result> result = List.of(new Result("Phone Number Save Successfully"));

            CustomResponse response = new CustomResponse(
                    result,
                    "N",
                    "N",
                    200,
                    "success"
            );

            return ResponseEntity.ok(List.of(response));
        } catch (IllegalArgumentException ex) {
          
            List<Result> result = List.of(new Result("Unable to save. Phone number is already linked to another user"));

            CustomResponse response = new CustomResponse(
                    result,
                    "Y",
                    ex.getMessage(),  
                    400,
                    "error"
            );

            return ResponseEntity.badRequest().body(List.of(response));
        } catch (IllegalStateException ex) {
          
            List<Result> result = List.of(new Result("User is inactive or not found. Cannot proceed with registration."));

            CustomResponse response = new CustomResponse(
                    result,
                    "Y",
                    ex.getMessage(), 
                    400,
                    "error"
            );

            return ResponseEntity.badRequest().body(List.of(response));
        }
    }




}
