package contact.phone.nabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import contact.phone.nabi.user.model.PhonebookUser;

public interface PhonebookUserRepository extends JpaRepository<PhonebookUser, Long> {
	
	   boolean existsByPhoneNumber(String phoneNumber);
	   
	  

}
