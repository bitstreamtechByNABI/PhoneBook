package contact.phone.nabi.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import contact.phone.nabi.PdfGenerator.PdfGenerator;
import contact.phone.nabi.emailt.emplate.EmailTemplateBuilder;
import contact.phone.nabi.repository.UserRepository;
import contact.phone.nabi.service.EmailService;
import contact.phone.nabi.service.LoginService;
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
import contact.phone.nabi.user.model.component.OtpHandler;
import contact.phone.nabi.user.model.verify.IpUtils;
import contact.phone.nabi.user.response.model.UserRegistrationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserRegistrationController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserRegistrationController.class); 

	@Autowired
	private OtpHandler otpHandler;

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
    
    
   
    
    @PostMapping("/verify-otp")
    public ResponseEntity<Map<String, Object>> verifyOtp(@RequestBody OtpRequest request, HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> resultEntry = new HashMap<>();

        boolean isValid = otpService.verifyOtp(request.getEmail(), request.getOtp());

        String ipAddress = IpUtils.getClientIp(httpRequest);
        String locationInfo = IpUtils.getLocation(ipAddress);

        String subject;
        String fullName = "User";
        String userId = "";

        if (isValid) {
            User user = userRepository.findByEmail(request.getEmail());
            fullName = user.getFirstName() + " " + user.getLastName();
            userId = user.getUserId();

            resultEntry.put("message", userService.getGreetingBasedOnTime() + ", " + fullName);
            resultEntry.put("UserId :", userId);
            response.put("message", "Login successful");
            response.put("status", otpHandler.getSuccessStatus());

            subject = "Login Attempt: Successful";
        } else {
            resultEntry.put("message", "Invalid or expired OTP");
            response.put("message", "Invalid or expired OTP");
            response.put("status", otpHandler.getFailureStatus());

            subject = "Login Attempt: Failed";
        }

        // ✅ Generate HTML body and PDF
        String htmlBody = EmailTemplateBuilder.buildLoginEmail(fullName, ipAddress, locationInfo, isValid);

        try {
            byte[] pdfBytes = PdfGenerator.generateLoginPdf(fullName, ipAddress, locationInfo, isValid);
            emailService.sendHtmlEmailWithPdf(request.getEmail(), subject, htmlBody, pdfBytes, "login-details.pdf");
        } catch (Exception e) {

            e.printStackTrace();
        }

        result.add(resultEntry);
        response.put("result", result);
        response.put("exceptionOccured", "");
        response.put("exceptionMessage", "");

        return ResponseEntity.ok(response);
    }


    
    @Autowired
    private LoginService loginService;
    
    
//    @PostMapping("/loginUserip")
//    public ResponseEntity<OtpLoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
//        OtpLoginResponse response = loginService.loginWithOtp(request, httpRequest);
//        return ResponseEntity
//            .status(response.getStatus() == 1 ? HttpStatus.OK :
//                   (response.getStatus() == 0 ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN))
//            .body(response);
//    }
//    
    
    @PostMapping(value = "/loginUserip", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OtpLoginResponse> loginWithoutPhoto(
        @RequestBody LoginRequest request,
        HttpServletRequest httpRequest) {

        OtpLoginResponse response = loginService.loginWithOtp(request, null, httpRequest);

        return ResponseEntity.status(
            response.getStatus() == 1 ? HttpStatus.OK :
            (response.getStatus() == 0 ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN))
            .body(response);
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
