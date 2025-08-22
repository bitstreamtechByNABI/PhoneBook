package contact.phone.nabi.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
	
	private static final Logger logger = LoggerFactory.getLogger(NotebookService.class); 
	
	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private NotebookRepository notebookRepository;
	    
/*
	    public NotebookEntry createNotebook(NotebookEntryRequest request) {
	        String userId = request.getUserId();
	        logger.info("Creating notebook entry for userId: {}", userId);

	        // 1. Check if user exists
	        if (!userRepository.existsByUserId(userId)) {
	            logger.warn("User not found: {}", userId);
	            throw new NotebookCreationException("User not found", null);
	        }

	        String status = userRepository.findUserStatusByUserIdStr(userId);
	        logger.debug("User status for {}: {}", userId, status);

	        if ("0".equalsIgnoreCase(status)) {
	            logger.warn("User is deactive: {}", userId);
	            throw new NotebookCreationException("User is deactive", null);
	        }

	        boolean exists = notebookRepository.findByUserIdAndNoteBookNameIgnoreCase(userId, request.getNoteBookName()).isPresent();
	        if (exists) {
	            logger.warn("Notebook name '{}' already exists for user {}", request.getNoteBookName(), userId);
	            throw new NotebookCreationException("Notebook name already exists for this user", null);
	        }

	        String encryptedContent = EncryptionUtil.encrypt(request.getNoteBookContent());
	        logger.debug("Notebook content encrypted for user {}", userId);

	        NotebookEntry entry = new NotebookEntry();
	        entry.setUserId(userId);
	        entry.setNoteStatus(request.getNoteStatus());
	        entry.setNoteBookName(request.getNoteBookName());
	        entry.setNoteBookContent(encryptedContent);
	        entry.setCreateDate(LocalDateTime.now());
	        entry.setUpdateDate(LocalDateTime.now());

	        NotebookEntry savedEntry = notebookRepository.save(entry);
	        logger.info("Notebook entry saved successfully for userId: {}, notebookName: {}", userId, request.getNoteBookName());

	        return savedEntry;
	    }

*/
	    
	    
	    public NotebookEntry createNotebook(NotebookEntryRequest request) {
	        String userId = request.getUserId();
	        logger.info("Creating notebook entry for userId: {}", userId);

	        // 1. Check if user exists
	        if (!userRepository.existsByUserId(userId)) {
	            logger.warn("User not found: {}", userId);
	            throw new NotebookCreationException("User not found", null);
	        }

	        String status = userRepository.findUserStatusByUserIdStr(userId);
	        logger.debug("User status for {}: {}", userId, status);

	        if ("0".equalsIgnoreCase(status)) {
	            logger.warn("User is deactive: {}", userId);
	            throw new NotebookCreationException("User is deactive", null);
	        }

	        // 2. Check if notebook with same name exists
	        boolean exists = notebookRepository
	                .findByUserIdAndNoteBookNameIgnoreCase(userId, request.getNoteBookName())
	                .isPresent();
	        if (exists) {
	            logger.warn("Notebook name '{}' already exists for user {}", request.getNoteBookName(), userId);
	            throw new NotebookCreationException("Notebook name already exists for this user", null);
	        }

	        // 3. Encrypt content
	        String encryptedContent = EncryptionUtil.encrypt(request.getNoteBookContent());
	        logger.debug("Notebook content encrypted for user {}", userId);

	        NotebookEntry entry = new NotebookEntry();
	        entry.setUserId(userId);
	        entry.setNoteStatus(request.getNoteStatus());
	        entry.setNoteBookName(request.getNoteBookName());
	        entry.setNoteBookContent(encryptedContent);

	        // 4. Handle optional attachment
	        if (request.getAttachmentBase64() != null && !request.getAttachmentBase64().isEmpty()) {
	            try {
	                byte[] fileBytes = java.util.Base64.getDecoder().decode(request.getAttachmentBase64());
	                entry.setAttachment(fileBytes);
	                entry.setAttachmentName(request.getAttachmentFileName());
	                entry.setAttachmentType(request.getAttachmentFileType());
	                logger.info("Attachment processed successfully for notebook '{}'", request.getNoteBookName());
	            } catch (IllegalArgumentException e) {
	                logger.error("Invalid Base64 attachment for user {}: {}", userId, e.getMessage());
	                throw new NotebookCreationException("Invalid attachment Base64 data", e);
	            }
	        } else {
	            logger.debug("No attachment provided for notebook '{}'", request.getNoteBookName());
	        }

	        NotebookEntry savedEntry = notebookRepository.save(entry);
	        logger.info("Notebook entry saved successfully for userId: {}, notebookName: {}", userId, request.getNoteBookName());

	        return savedEntry;
	    }

	    
	    
	    /*
	    public NotebookEntry createNotebook(NotebookEntryRequest request, MultipartFile file) {
	        String userId = request.getUserId();

	        if (!userRepository.existsByUserId(userId)) {
	            throw new RuntimeException("User not found");
	        }

	        String status = userRepository.findUserStatusByUserIdStr(userId);
	        if ("0".equalsIgnoreCase(status)) {
	            throw new RuntimeException("User is deactive");
	        }

	        boolean exists = notebookRepository.findByUserIdAndNoteBookNameIgnoreCase(userId, request.getNoteBookName())
	                .isPresent();
	        if (exists) {
	            throw new RuntimeException("Notebook name already exists for this user");
	        }

	        String encryptedContent = EncryptionUtil.encrypt(request.getNoteBookContent());

	        NotebookEntry entry = new NotebookEntry();
	        entry.setUserId(userId);
	        entry.setNoteStatus(request.getNoteStatus());
	        entry.setNoteBookName(request.getNoteBookName());
	        entry.setNoteBookContent(encryptedContent);

	        // Handle optional file
	        if (file != null && !file.isEmpty()) {
	            try {
	                entry.setAttachment(file.getBytes());
	                entry.setAttachmentName(file.getOriginalFilename());
	                entry.setAttachmentType(file.getContentType());
	            } catch (IOException e) {
	                throw new RuntimeException("Failed to process uploaded file", e);
	            }
	        }

	        return notebookRepository.save(entry);
	    }
*/
	    public Page<NotebookResponseDTO> getNotebooks(String userId, String noteBookName, Integer id, int page, int size) {
	        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());
	        Page<NotebookEntry> resultPage;

	        if (userId != null && !userId.isEmpty()) {
	            if (!isUserExists(userId)) {
	                throw new NotebookFetchException("Notebook fetch failed: User does not exist", null);
	            }
	            if (!isUserActive(userId)) {
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

	        List<NotebookResponseDTO> activeNoteDTOs = resultPage.getContent().stream()
	                .filter(entry -> "ACTIVE".equalsIgnoreCase(entry.getNoteStatus()))
	                .map((NotebookEntry entry) -> {
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
	                            decryptedContent,
	                            entry.getAttachment() != null ? entry.getAttachment() : new byte[0],
	                            entry.getAttachmentName(),
	                            entry.getAttachmentType()
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
	                    decryptedContent, null, decryptedContent, decryptedContent
	                );
	            })
	            .collect(Collectors.toList());
	    }



		public List<NotebookEntry> getNotesByNoteBookName(String noteBookName) {
			return notebookRepository.findNotesByNoteBookName(noteBookName);
		}


}
