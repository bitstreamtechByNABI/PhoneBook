package contact.phone.nabi.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import contact.phone.nabi.service.UserServicePhoneNumber;
import contact.phone.nabi.user.model.ApiResponsePhoneNumber;
import contact.phone.nabi.user.model.ContactUpdateRequest;
import contact.phone.nabi.user.model.FinalResponse;
import contact.phone.nabi.user.model.SoftDeleteApiResponsePhoneNumber;
import contact.phone.nabi.user.model.UserResponse;

@RestController
@RequestMapping("/api/users/phonebook")
public class GetUserPhoneNumberController {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(GetUserPhoneNumberController.class); 
	
	@Autowired
	private UserServicePhoneNumber userService;
		
	 @GetMapping("/get-user-details")
	    public ResponseEntity<ApiResponsePhoneNumber> getUserDetails(@RequestParam("userId") String userId) {
	        try {
	            List<UserResponse> users = userService.getUsersByUserId(userId);

	            FinalResponse result = new FinalResponse(userId, users);

	            ApiResponsePhoneNumber response = new ApiResponsePhoneNumber(
	                result,
	                "N",
	                "N",
	                200,
	                "success"
	            );

	            return ResponseEntity.ok(response);

	        } catch (Exception e) {
	        	ApiResponsePhoneNumber errorResponse = new ApiResponsePhoneNumber(
	                null,
	                "Y",
	                e.getMessage(),
	                500,
	                "error"
	            );
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	        }
	    }
	 
	 
	 @PutMapping("/soft-delete/{phoneNumber}")
	 public ResponseEntity<SoftDeleteApiResponsePhoneNumber> softDeleteContact(@PathVariable String phoneNumber) {
	     try {
	         boolean deleted = userService.softDeleteByPhoneNumber(phoneNumber);

	         SoftDeleteApiResponsePhoneNumber response;
	         if (deleted) {
	             Map<String, String> data = Collections.singletonMap(
	                 "Deleted",
	                 "Contact deleted successfully. +91" + phoneNumber
	             );

	             response = new SoftDeleteApiResponsePhoneNumber(
	                     data,
	                     "N",
	                     "N",
	                     200,
	                     "Contact deleted successfully."
	             );
	             return ResponseEntity.ok(response);

	         } else {
	             Map<String, String> data = Collections.singletonMap(
	                 "Deleted",
	                 "Contact not found or could not be deleted. +" + phoneNumber
	             );

	             response = new SoftDeleteApiResponsePhoneNumber(
	                     data,
	                     "N",
	                     "N",
	                     404,
	                     "Contact not found or could not be deleted."
	             );
	             return ResponseEntity.status(404).body(response);
	         }

	     } catch (Exception e) {
	         Map<String, String> data = Collections.singletonMap(
	             "Deleted",
	             "Internal server error. +" + phoneNumber
	         );

	         SoftDeleteApiResponsePhoneNumber errorResponse = new SoftDeleteApiResponsePhoneNumber(
	                 data,
	                 "N",
	                 "N",
	                 500,
	                 "Internal server error: " + e.getMessage()
	         );
	         return ResponseEntity.status(500).body(errorResponse);
	     }
	 }

	 
	 
	 @PutMapping("/update-by-phone/{phoneNumber}")
	 public ResponseEntity<SoftDeleteApiResponsePhoneNumber> updateContactByPhoneNumber(
	         @PathVariable String phoneNumber,
	         @RequestBody ContactUpdateRequest request) {

	     try {
	         if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
	             return ResponseEntity.badRequest().body(
	                 new SoftDeleteApiResponsePhoneNumber(
	                     Collections.singletonMap("Updated", "Phone number is required"),
	                     "N", "N", 400, "Invalid phone number"
	                 )
	             );
	         }

	         boolean updated = userService.updateContactByPhoneNumber(phoneNumber, request);

	         if (updated) {
	             return ResponseEntity.ok(
	                 new SoftDeleteApiResponsePhoneNumber(
	                     Collections.singletonMap("Updated", "Contact updated successfully. +91" + phoneNumber),
	                     "N", "N", 200, "Contact updated successfully."
	                 )
	             );
	         } else {
	             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	                 new SoftDeleteApiResponsePhoneNumber(
	                     Collections.singletonMap("Updated", "Contact not found or could not be updated. +91" + phoneNumber),
	                     "N", "N", 404, "Contact not found or could not be updated."
	                 )
	             );
	         }

	     } catch (Exception e) {
	         logger.error("Error updating contact for phone: {}", phoneNumber, e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
	             new SoftDeleteApiResponsePhoneNumber(
	                 Collections.singletonMap("Updated", "Internal server error. +91" + phoneNumber),
	                 "N", "N", 500, "Internal server error: " + e.getMessage()
	             )
	         );
	     }
	 }

	 
	 

}
