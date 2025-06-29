package contact.phone.nabi.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import contact.phone.nabi.repository.OtpLogRepo;
import contact.phone.nabi.user.model.OtpLog;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class OtpService {
	  private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
	
    @Autowired
    private OtpLogRepo otpLogRepository;
    
    
	
	 private final Map<String, String> otpStorage = new HashMap<>();

	 public String generateOtp(String email) {
		    int otpLength = 6;
		    String chars = "ABCDEFGHIjklmnopqrstuvwxyz0123456789";
		    StringBuilder otp = new StringBuilder();

		    Random random = new Random();
		    for (int i = 0; i < otpLength; i++) {
		        otp.append(chars.charAt(random.nextInt(chars.length())));
		    }

		    otpStorage.put(email, otp.toString());
		    return otp.toString();
		}  
	 


	 public boolean verifyOtp(String email, String inputOtp) {
		    String normalizedEmail = email.toLowerCase();

		    // Get OTP from DB only
		    String dbOtp = otpLogRepository.getotpByEmail(normalizedEmail);

		    logger.debug("DB OTP: {}", dbOtp);
		    logger.debug("Input OTP: {}", inputOtp);

		    // Check: dbOtp is not null, not empty, and matches input
		    if (dbOtp != null && !dbOtp.trim().isEmpty() && dbOtp.trim().equals(inputOtp.trim())) {
		        logger.info("OTP verified successfully for {}", normalizedEmail);
		        return true;
		    }

		    logger.warn("OTP verification failed for {}", normalizedEmail);
		    return false;
		}

	 

	 public void saveOtpDetails(String userId, String email, String otp, String ipAddress, String deviceName, String macAddress) {
		    OtpLog log = new OtpLog();
		    log.setUserId(userId);  
		    log.setEmail(email);
		    log.setOtp(otp);
		    log.setIpAddress(ipAddress);
		    log.setDeviceName(deviceName);
		    log.setMacAddress(macAddress);
		    log.setCreatedAt(LocalDateTime.now());

		    otpLogRepository.save(log);
		}
	 
	 
	 public String getMacAddress() {
		    try {
		        InetAddress localHost = InetAddress.getLocalHost();
		        NetworkInterface network = NetworkInterface.getByInetAddress(localHost);

		        if (network == null) {
		            return "MAC address not found";
		        }

		        byte[] mac = network.getHardwareAddress();

		        if (mac == null) {
		            return "MAC address not available";
		        }

		        StringBuilder sb = new StringBuilder();
		        for (int i = 0; i < mac.length; i++) {
		            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
		        }
		        return sb.toString();

		    } catch (Exception e) {
		        e.printStackTrace();
		        return "Error retrieving MAC address";
		    }
		}


	  public void logOtpEvent(String userId, String username, String email, String otp, HttpServletRequest request) {
	        String ipAddress = request.getRemoteAddr();
	        String deviceName = "UNKNOWN";
	        String macAddress = "UNKNOWN";

	        try {
	            InetAddress inetAddress = InetAddress.getLocalHost();
	            deviceName = inetAddress.getHostName();
	            NetworkInterface network = NetworkInterface.getByInetAddress(inetAddress);

	            if (network != null) {
	                byte[] mac = network.getHardwareAddress();
	                if (mac != null) {
	                    StringBuilder sb = new StringBuilder();
	                    for (int i = 0; i < mac.length; i++) {
	                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
	                    }
	                    macAddress = sb.toString();
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        OtpLog log = new OtpLog();
	        log.setUserId(userId);
	        log.setUsername(username);
	        log.setEmail(email);
	        log.setOtp(otp);
	        log.setIpAddress(ipAddress);
	        log.setDeviceName(deviceName);
	        log.setMacAddress(macAddress);
	        log.setCreatedAt(LocalDateTime.now());

	        otpLogRepository.save(log);
	    }



}
