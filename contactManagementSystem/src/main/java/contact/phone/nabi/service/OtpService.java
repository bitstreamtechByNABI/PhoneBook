package contact.phone.nabi.service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import contact.phone.nabi.repository.OtpLogRepo;
import contact.phone.nabi.user.model.OtpLog;

@Service
public class OtpService {
	
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
		    if (otpStorage.containsKey(email) && otpStorage.get(email).equals(inputOtp)) {
		        otpStorage.remove(email);
		        return true;
		    }
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




}
