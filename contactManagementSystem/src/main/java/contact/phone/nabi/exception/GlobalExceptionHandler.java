package contact.phone.nabi.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import contact.phone.nabi.user.model.OtpLoginResponse;
import contact.phone.nabi.user.model.Notebook.NotedBookResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<OtpLoginResponse> handleIllegalArgument(IllegalArgumentException ex) {
        OtpLoginResponse response = new OtpLoginResponse();
        response.setResult(Collections.singletonList(Collections.singletonMap("otpStatus", "Invalid input")));
        response.setStatus(0);
        response.setExceptionOccured("true");
        response.setMessage("Bad request");
        response.setExceptionMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotebookCreationException.class)
    public ResponseEntity<NotedBookResponse> handleNotebookException(NotebookCreationException ex) {
    	NotedBookResponse response = new NotedBookResponse(
                null,
                500,
                "Notebook creation failed: " + ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<NotedBookResponse> handleGeneralException(Exception ex) {
    	NotedBookResponse response = new NotedBookResponse(
                null,
                500,
                 ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<NotedBookResponse> handleGenericException(Exception ex) {
//        NotedBookResponse response = new NotedBookResponse(
//            null,
//            500,
//            ex.getMessage() != null ? ex.getMessage() : "Unexpected error occurred"
//        );
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }

}
