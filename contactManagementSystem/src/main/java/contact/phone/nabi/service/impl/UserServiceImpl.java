package contact.phone.nabi.service.impl;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import contact.phone.nabi.repository.OtpLogRepo;
import contact.phone.nabi.repository.PhonebookUserRepository;
import contact.phone.nabi.repository.UserRepository;
import contact.phone.nabi.service.UserService;
import contact.phone.nabi.user.model.PhoneBookRequest;
import contact.phone.nabi.user.model.PhonebookUser;
import contact.phone.nabi.user.model.User;
import contact.phone.nabi.user.model.UserRegistrationRequest;
import contact.phone.nabi.user.model.UserResult;
import contact.phone.nabi.user.response.model.UserRegistrationResponse;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
   
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OtpLogRepo otpLogRepo;
    
    @Autowired
    private PhonebookUserRepository phonebookUserRepository;

    @Override
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUserName(request.getUserName());
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());
        user.setSecurityQuestions(request.getSecurityQuestions());

        // Parse DOB
        try {
            if (request.getDob() != null && !request.getDob().trim().isEmpty()) {
                LocalDate dob = LocalDate.parse(request.getDob().trim(), DateTimeFormatter.ISO_LOCAL_DATE);
                user.setDob(dob);
            } else {
                user.setDob(null);
            }
        } catch (DateTimeParseException e) {
            return buildErrorResponse("Invalid date format for dob. Expected format: yyyy-MM-dd");
        }

        // Regex patterns
        Pattern phoneDigitsPattern = Pattern.compile("^[6-9]\\d{9}$");
        Pattern phoneAlphaNumPattern = Pattern.compile("^[a-zA-Z0-9]{6,9}$");
        Pattern emailPattern = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
        Pattern noSpecialCharPattern = Pattern.compile("^[\\p{Alnum}]+$");

        // Validate phone
        String phone = request.getPhone();
        if (phone == null || phone.trim().isEmpty()) {
            user.setPhone(null);
        } else {
            phone = phone.trim();
            if (phoneDigitsPattern.matcher(phone).matches() || phoneAlphaNumPattern.matcher(phone).matches()) {
                user.setPhone(phone);
            } else {
                return buildErrorResponse("Invalid phone number. Must be exactly 10 digits or alphanumeric 6-9 characters without special characters or emojis.");
            }
        }

        // Validate email
        String email = request.getEmail();
        if (email == null || email.trim().isEmpty()) {
            user.setEmail(email);
        } else {
            email = email.trim();
            if (!emailPattern.matcher(email).matches()) {
                return buildErrorResponse("Invalid email format.");
            }
            user.setEmail(email);
        }

        // Validate username
        String userName = request.getUserName();
        if (userName != null && !userName.trim().isEmpty()) {
            userName = userName.trim();
            if (!noSpecialCharPattern.matcher(userName).matches()) {
                return buildErrorResponse("Username must not contain special characters or emojis.");
            }
            user.setUserName(userName);
        }

        // Check required fields
        List<String> missingFields = new ArrayList<>();

        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty())
            missingFields.add("firstName");
        if (user.getLastName() == null || user.getLastName().trim().isEmpty())
            missingFields.add("lastName");
        if (user.getEmail() == null || user.getEmail().trim().isEmpty())
            missingFields.add("email");
        if (request.getPassword() == null || request.getPassword().trim().isEmpty())
            missingFields.add("password");
        if (user.getPhone() == null || user.getPhone().trim().isEmpty())
            missingFields.add("phone");
        if (user.getDob() == null)
            missingFields.add("dob");
        if (user.getGender() == null || user.getGender().trim().isEmpty())
            missingFields.add("gender");
        if (user.getAddress() == null)
            missingFields.add("address");
        if (user.getSecurityQuestions() == null || user.getSecurityQuestions().isEmpty())
            missingFields.add("securityQuestions");
        if (user.getUserName() == null || user.getUserName().trim().isEmpty())
            missingFields.add("userName");

        if (!missingFields.isEmpty()) {
            return buildErrorResponse("Missing required fields: " + String.join(", ", missingFields));
        }

        // Check for duplicates
        if (userRepository.existsByUserName(user.getUserName())) {
            return buildErrorResponse("Username is already taken");
        }
        if (userRepository.existsByPhone(user.getPhone())) {
            return buildErrorResponse("Phone is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return buildErrorResponse("Email is already registered");
        }

        // Generate unique 6-digit userId
        String userId;
        do {
            userId = generateAlphaNumericId(6);
        } while (userRepository.existsByUserId(userId));
        user.setUserId(userId);

        // Encode password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        LOGGER.info("Registering user with username: {}, email: {}, encodedPassword: {}, phone: {}, dob: {}, gender: {}",
                user.getUserName(), user.getEmail(), user.getPassword(),
                user.getPhone(), user.getDob(), user.getGender());

        // Save user
        userRepository.save(user);

        return buildSuccessResponse(user.getUserId(), "User registered successfully");
    }

    private UserRegistrationResponse buildErrorResponse(String errorMessage) {
        UserResult result = new UserResult();
        result.setMessage("Error: " + errorMessage);

        UserRegistrationResponse response = new UserRegistrationResponse();
        response.setResult(List.of(result));
        response.setExceptionOccured("Y");
        response.setExceptionMessage(errorMessage);
        response.setStatus(400);
        response.setMessage("failure");

        return response;
    }

    private UserRegistrationResponse buildSuccessResponse(String userId, String message) {
        UserResult result = new UserResult();
        result.setUserId(userId);
        result.setMessage(message);

        UserRegistrationResponse response = new UserRegistrationResponse();
        response.setResult(List.of(result));
        response.setExceptionOccured("N");
        response.setExceptionMessage("N");
        response.setStatus(200);
        response.setMessage("success");

        return response;
    }


    // Helper method to build a consistent response object
    private UserRegistrationResponse buildResponse(String userId, String message) {
        UserResult result = new UserResult();
        result.setUserId(userId);
        result.setMessage(message);
        UserRegistrationResponse response = new UserRegistrationResponse();
        response.setResult(List.of(result));
        response.setExceptionOccured("N");
        response.setExceptionMessage("N");
        response.setStatus(200);
        response.setMessage("success");

        return response;
    }

    // Generate numeric userId (digits only)
    private String generateAlphaNumericId(int length) {
        String chars = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    @Override
    public boolean isUserValid(String username, String rawPassword) {
        LOGGER.info("Validating credentials for username: {}", username);
        System.out.println("UserName "+username+"password"+ rawPassword);
        // Fetch encoded password from DB
        String encodedPassword = userRepository.getPasswordHash(username);
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            LOGGER.warn("No password found for username: {}", username);
            return false;
        }
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        if (!matches) {
            LOGGER.warn("Password mismatch for username: {}", username);
        } else {
            LOGGER.info("Password match successful for username: {}", username);
        }

        return matches;
    }


    @Override
    public String getEmailByUsername(String username) {
        LOGGER.info("Fetching email for username: {}", username);
        User user = userRepository.findByUserName(username);

        if (user == null) {
            LOGGER.warn("No user found with username: {}", username);
            return null;
        }

        LOGGER.info("Email fetched successfully for username: {}", username);
        return user.getEmail();
    }

    public String getGreetingBasedOnTime() {
        LocalTime currentTime = LocalTime.now();

        if (currentTime.isAfter(LocalTime.of(5, 0)) && currentTime.isBefore(LocalTime.of(12, 0))) {
            return "Good Morning";
        } else if (currentTime.isAfter(LocalTime.of(12, 0)) && currentTime.isBefore(LocalTime.of(17, 0))) {
            return "Good Afternoon";
        } else if (currentTime.isAfter(LocalTime.of(17, 0)) && currentTime.isBefore(LocalTime.of(21, 0))) {
            return "Good Evening";
        } else {
            return "Good Night";
        }
    }

  
    public PhonebookUser saveUserIfActive(PhoneBookRequest request) {
        String userId = request.getUserId();
        Boolean userStatus = userRepository.findUserStatusByUserId(userId);

        if (!Boolean.TRUE.equals(userStatus)) {
            throw new IllegalStateException("User is INACTIVE or not found in the system");
        }

        boolean phoneExists = phonebookUserRepository.existsByPhoneNumber(request.getPhoneNumber());
        if (phoneExists) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        PhonebookUser user = new PhonebookUser();
        user.setUserId(request.getUserId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        return phonebookUserRepository.save(user);
    }

}
