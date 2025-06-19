package contact.phone.nabi.Controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import contact.phone.nabi.service.UserServicePhoneNumber;
import contact.phone.nabi.user.model.ApiResponsePhoneNumber;
import contact.phone.nabi.user.model.FinalResponse;
import contact.phone.nabi.user.model.UserResponse;

@RestController
@RequestMapping("/api/users/phonebook")
public class GetUserPhoneNumberController {
	
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


}
