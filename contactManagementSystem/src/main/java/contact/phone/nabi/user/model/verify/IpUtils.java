package contact.phone.nabi.user.model.verify;

import org.springframework.web.client.RestTemplate;

import contact.phone.nabi.user.model.IpLocation.IpLocation;
import jakarta.servlet.http.HttpServletRequest;

public class IpUtils {
	
	 private static final RestTemplate rest = new RestTemplate();
	
	 public static String getClientIp(HttpServletRequest request) {
	        String xfHeader = request.getHeader("X-Forwarded-For");
	        if (xfHeader == null) {
	            return request.getRemoteAddr();
	        }
	        return xfHeader.split(",")[0];
	    }


	 public static String getLocation(String ip) {
	        try {
	            String url = "https://ipapi.co/" + ip + "/json/";
	            IpLocation location = rest.getForObject(url, IpLocation.class);

	            if (location != null) {
	                return String.format("%s, %s, %s",
	                        safe(location.getCity()),
	                        safe(location.getRegion()),
	                        safe(location.getCountry_name()));
	            }
	        } catch (Exception e) {
	           e.printStackTrace();
	        }
	        return "Unknown Location";
	    }

	    private static String safe(String value) {
	        return (value != null && !value.isEmpty()) ? value : "-";
	    }
}
