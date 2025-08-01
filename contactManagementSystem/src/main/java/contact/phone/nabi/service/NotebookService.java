package contact.phone.nabi.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import contact.phone.nabi.cipher.EncryptionUtil;
import contact.phone.nabi.exception.NotebookCreationException;
import contact.phone.nabi.exception.NotebookFetchException;
import contact.phone.nabi.repository.NotebookRepository;
import contact.phone.nabi.repository.UserRepository;
import contact.phone.nabi.user.model.Notebook.NotebookEntry;
import contact.phone.nabi.user.model.Notebook.NotebookEntryRequest;
import contact.phone.nabi.user.model.Notebook.dto.NotebookResponseDTO;

@Service
public class NotebookService {
	
	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private NotebookRepository notebookRepository;
	    
	    
	    public NotebookEntry createNotebook(NotebookEntryRequest request) {
	        String userId = request.getUserId();
	       	        // 1. Check if user exists
	        if (!userRepository.existsByUserId(userId)) {
	            throw new NotebookCreationException("User not found", null);
	        }

	        String status = userRepository.findUserStatusByUserIdStr(userId);

	        if ("0".equalsIgnoreCase(status)) {
	            throw new NotebookCreationException("User is deactive", null);
	        }

	        boolean exists = notebookRepository.findByUserIdAndNoteBookNameIgnoreCase(userId, request.getNoteBookName()).isPresent();
	        if (exists) {
	            throw new NotebookCreationException("Notebook name already exists for this user", null);
	        }

	        String encryptedContent = EncryptionUtil.encrypt(request.getNoteBookContent());
	        NotebookEntry entry = new NotebookEntry();
	        entry.setUserId(userId);
	        entry.setNoteStatus(request.getNoteStatus());
	        entry.setNoteBookName(request.getNoteBookName());
	        entry.setNoteBookContent(encryptedContent);
	        entry.setCreateDate(LocalDateTime.now());
	        entry.setUpdateDate(LocalDateTime.now());

	        return notebookRepository.save(entry);
	    }



	    public Page<NotebookResponseDTO> getNotebooks(String userId, String noteBookName, Integer id, int page, int size) {
	        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());
	        Page<NotebookEntry> resultPage;

	        if (userId != null && !userId.isEmpty()) {
	            boolean userExists = userRepository.existsByUserId(userId);
	            if (!userExists) {
	                throw new NotebookFetchException("Notebook fetch failed: User does not exist", null);
	            }

	            Boolean status = userRepository.findUserStatusByUserId(userId);
	            if (status == null || !status) {
	                throw new NotebookFetchException("Notebook fetch failed: User is inactive", null);
	            }

	            resultPage = notebookRepository.findByUserId(userId, pageable);
	        } else if (noteBookName != null && !noteBookName.isEmpty()) {
	            resultPage = notebookRepository.findByNoteBookNameContainingIgnoreCase(noteBookName, pageable);
	        } else if (id != null) {
	            Optional<NotebookEntry> optionalEntry = notebookRepository.findById(id);
	            if (optionalEntry.isPresent()) {
	                resultPage = new PageImpl<>(Collections.singletonList(optionalEntry.get()), pageable, 1);
	            } else {
	                return Page.empty(pageable);
	            }
	        } else {
	            resultPage = notebookRepository.findAll(pageable);
	        }

	        // 🔐 Decrypt + Filter + Map to DTO
	        List<NotebookResponseDTO> activeNoteDTOs = resultPage.getContent().stream()
	            .filter(entry -> "ACTIVE".equalsIgnoreCase(entry.getNoteStatus()))
	            .map(entry -> {
	                String decryptedContent;
	                try {
	                    decryptedContent = EncryptionUtil.decrypt(entry.getNoteBookContent());
	                } catch (Exception e) {
	                    decryptedContent = "**Decryption Failed**";
	                }

	                return new NotebookResponseDTO(
	                    entry.getUserId(),
	                    entry.getNoteStatus(),
	                    entry.getNoteBookName(),
	                    decryptedContent
	                );
	            })
	            .collect(Collectors.toList());

	        return new PageImpl<>(activeNoteDTOs, pageable, activeNoteDTOs.size());
	    }


	    public boolean isUserExists(String userId) {
	        return userRepository.existsByUserId(userId);
	    }


	    public boolean isUserActive(String userId) {
	        Boolean status = userRepository.findUserStatusByUserId(userId);
	        return status != null && status; 
	    }



	    public List<NotebookResponseDTO> getNotesByUser(String userId) {
	        // 1. Check if user exists
	        boolean userExists = userRepository.existsByUserId(userId);
	        if (!userExists) {
	            throw new NotebookFetchException("User not found", null);
	        }

	        // 2. Check if user is active
	        Boolean status = userRepository.findUserStatusByUserId(userId);
	        if (status == null || !status) {
	            throw new NotebookFetchException("User is inactive", null);
	        }

	        // 3. Fetch notes by userId ordered by createDate descending
	        List<NotebookEntry> entries = notebookRepository.findByUserIdOrderByCreateDateDesc(userId);

	        // 4. Filter ACTIVE notes and decrypt content
	        return entries.stream()
	            .filter(entry -> "ACTIVE".equalsIgnoreCase(entry.getNoteStatus()))
	            .map(entry -> {
	                String decryptedContent;
	                try {
	                    decryptedContent = EncryptionUtil.decrypt(entry.getNoteBookContent());
	                } catch (Exception e) {
	                    decryptedContent = "**Decryption Failed**";
	                }

	                return new NotebookResponseDTO(
	                    entry.getUserId(),
	                    entry.getNoteStatus(),
	                    entry.getNoteBookName(),
	                    decryptedContent
	                );
	            })
	            .collect(Collectors.toList());
	    }


}
