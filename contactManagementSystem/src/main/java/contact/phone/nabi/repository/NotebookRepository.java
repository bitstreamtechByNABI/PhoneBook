package contact.phone.nabi.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import contact.phone.nabi.user.model.Notebook.NotebookEntry;

public interface NotebookRepository extends JpaRepository<NotebookEntry, Integer> {

	Optional<NotebookEntry> findByUserIdAndNoteBookNameIgnoreCase(String userId, String noteBookName);

	Page<NotebookEntry> findByUserId(String userId, Pageable pageable);

	Page<NotebookEntry> findByNoteBookNameContainingIgnoreCase(String noteBookName, Pageable pageable);

}
