package contact.phone.nabi.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import contact.phone.nabi.repository.CommanRepo;
import contact.phone.nabi.user.model.UserResponse;

@Service
public class UserServicePhoneNumber {
	private static final Logger logger = LoggerFactory.getLogger(UserServicePhoneNumber.class); 
	
	@Autowired
	private CommanRepo   phonebookUserRepository;

	

	public List<UserResponse> getUsersByUserId(String userId) {
	    List<UserResponse> users = new ArrayList<>();

	    try {
	        List<Object[]> rawResults = phonebookUserRepository.findUserDetailsByUserId(userId);

	        if (rawResults == null || rawResults.isEmpty()) {
	            logger.warn("No user data found for userId: {}", userId);
	            return users; // return empty list
	        }

	        for (Object[] row : rawResults) {
	            String email = row[0] != null ? row[0].toString() : "";
	            String firstName = row[1] != null ? row[1].toString() : "";
	            String lastName = row[2] != null ? row[2].toString() : "";
	            String phoneNumber = row[3] != null ? row[3].toString() : "";

	            users.add(new UserResponse(firstName, lastName, email, phoneNumber));
	        }

	    } catch (DataAccessException dae) {
	        logger.error("Database access error for userId {}: {}", userId, dae.getMessage(), dae);
	        throw new RuntimeException("Database error occurred. Please try again later.");

	    } catch (NullPointerException npe) {
	        logger.error("Null pointer exception while processing userId {}: {}", userId, npe.getMessage(), npe);
	        throw new RuntimeException("Unexpected error while processing data.");

	    } catch (Exception e) {
	        logger.error("Unhandled exception for userId {}: {}", userId, e.getMessage(), e);
	        throw new RuntimeException("Internal server error. Please try again.");
	    }

	    return users;
	}



}
