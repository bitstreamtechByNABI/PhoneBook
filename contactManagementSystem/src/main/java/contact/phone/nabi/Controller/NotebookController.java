package contact.phone.nabi.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import contact.phone.nabi.exception.NotebookFetchException;
import contact.phone.nabi.service.NotebookService;
import contact.phone.nabi.user.model.Notebook.NotebookEntry;
import contact.phone.nabi.user.model.Notebook.NotebookEntryRequest;
import contact.phone.nabi.user.model.Notebook.NotebookListResponse;
import contact.phone.nabi.user.model.Notebook.NotedBookResponse;
import contact.phone.nabi.user.model.Notebook.dto.NotebookResponseDTO;
import contact.phone.nabi.user.response.NotedBookResponseGetNote;

@RestController
@RequestMapping("/phonebook/noted")
public class NotebookController {

	@Autowired
	private NotebookService notebookService;
   
	  @PostMapping("/create")
	    public ResponseEntity<NotedBookResponse> createNotedBook(@RequestBody NotebookEntryRequest request) {
	        NotebookEntry entry = notebookService.createNotebook(request);

	        Map<String, Object> responseData = new HashMap<>();
	        responseData.put("noteBookName", entry.getNoteBookName());

	        return ResponseEntity.status(HttpStatus.OK)
	                .body(new NotedBookResponse(responseData, 200, "Notebook created successfully"));
	    }
	  
	  
	  @GetMapping("/noted/get")
	  public ResponseEntity<NotebookListResponse> getNotebooks(
	          @RequestParam(required = false) String userId,
	          @RequestParam(required = false) String noteBookName,
	          @RequestParam(required = false) Integer id,
	          @RequestParam(defaultValue = "0") int page,
	          @RequestParam(defaultValue = "10") int size
	  ) {
	      Page<NotebookResponseDTO> pageResult = notebookService.getNotebooks(userId, noteBookName, id, page, size);

	      NotebookListResponse response = new NotebookListResponse(
	              pageResult.getContent(),  
	              200,
	              "Data fetched successfully"
	      );

	      return ResponseEntity.ok(response);
	  }

	  @GetMapping("/get-user-notes")
	  public ResponseEntity<NotedBookResponseGetNote> getUserNotes(@RequestParam("userId") String userId) {
	      // 1. Check if user exists
	      if (!notebookService.isUserExists(userId)) {
	          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
	              new NotedBookResponseGetNote(
	                  null,
	                  "User not found",
	                  404,
	                  "Failure"
	              )
	          );
	      }

	      // 2. Check if user is active
	      if (!notebookService.isUserActive(userId)) {
	          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
	              new NotedBookResponseGetNote(
	                  null,
	                  "User is not active",
	                  403,
	                  "Inactive User"
	              )
	          );
	      }

	      // 3. Get all notes for the user
	      List<NotebookResponseDTO> notes = notebookService.getNotesByUser(userId);

	      return ResponseEntity.ok(
	          new NotedBookResponseGetNote(
	              notes,
	              null,
	              200,
	              "Notes retrieved successfully"
	          )
	      );
	  }

//	  @PutMapping("/delete-note/{id}")
//	  public ResponseEntity<String> softDeleteNote(@PathVariable int id) {
//	      try {
//	          notebookService.softDeleteNotebookById(id);
//	          return ResponseEntity.ok("Note deleted successfully");
//	      } catch (NotebookFetchException e) {
//	          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//	      }
//	  }



}
