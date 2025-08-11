package contact.phone.nabi.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import contact.phone.nabi.repository.CommanRepo;
import contact.phone.nabi.user.model.ContactUpdateRequest;
import contact.phone.nabi.user.model.UserResponse;
import jakarta.transaction.Transactional;

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
	            return users;
	        }

	        for (Object[] row : rawResults) {
	            String email = row[0] != null ? row[0].toString() : "";
	            String firstName = row[1] != null ? row[1].toString() : "";
	            String lastName = row[2] != null ? row[2].toString() : "";
	            String phoneNumber = row[3] != null ? row[3].toString() : "";

	            String base64Image = "";
	            if (row[4] != null && row[4] instanceof byte[]) {
	                byte[] imageBytes = (byte[]) row[4];
	                base64Image = Base64.getEncoder().encodeToString(imageBytes);
	            }

	            users.add(new UserResponse(firstName, lastName, email, phoneNumber, base64Image));
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


	 public boolean softDeleteByPhoneNumber(String phoneNumber) {
	        try {
	            int rowsUpdated = phonebookUserRepository.findByPhoneNumber(phoneNumber);

	            if (rowsUpdated > 0) {
	                logger.info("Contact with phone number {} soft-deleted successfully.", phoneNumber);
	                return true;
	            } else {
	                logger.warn("No contact found for phone number: {}", phoneNumber);
	                return false;
	            }

	        } catch (Exception e) {
	            logger.error("Error while soft deleting phone number {}: {}", phoneNumber, e.getMessage(), e);
	            return false;
	        }
	    }


	 @Transactional
	 public boolean updateContactByPhoneNumber(String phoneNumber, ContactUpdateRequest request) {
	     byte[] imageBytes = null;

	     if (request.getImage() != null && !request.getImage().isEmpty()) {
	         try {
	             String base64Image = request.getImage().trim();

	             if (base64Image.contains(",")) {
	                 base64Image = base64Image.split(",")[1];
	             }

	             int padding = 4 - (base64Image.length() % 4);
	             if (padding < 4) {
	                 base64Image += "=".repeat(padding);
	             }

	             imageBytes = Base64.getDecoder().decode(base64Image);

	         } catch (IllegalArgumentException e) {
	             throw new RuntimeException("Invalid Base64 image data for phone number: " + phoneNumber, e);
	         }
	     }

	     int updatedRows = phonebookUserRepository.updateContactDetails(
	         phoneNumber,
	         request.getFirstName(),
	         request.getLastName(),
	         request.getEmail(),
	         imageBytes
	     );

	     return updatedRows > 0;
	 }


}
