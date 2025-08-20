package contact.phone.nabi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import contact.phone.nabi.user.model.Notebook.NotebookEntry;

public interface NotebookRepository extends JpaRepository<NotebookEntry, Integer> {

	Optional<NotebookEntry> findByUserIdAndNoteBookNameIgnoreCase(String userId, String noteBookName);

	Page<NotebookEntry> findByUserId(String userId, Pageable pageable);

	Page<NotebookEntry> findByNoteBookNameContainingIgnoreCase(String noteBookName, Pageable pageable);
	List<NotebookEntry> findByUserId(String userId);

	List<NotebookEntry> findByUserIdOrderByCreateDateDesc(String userId);

	@Query(value = "SELECT * FROM Notebook_User WHERE note_book_name = ?1", nativeQuery = true)
	List<NotebookEntry> findNotesByNoteBookName(String noteBookName);
	


}
