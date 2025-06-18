package contact.phone.nabi.user.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "phonebook")
public class PhonebookUser {
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @NotBlank(message = "userId is mandatory")
	    @Column(length = 20, unique = true)
	    private String userId;

	    @Column(length = 100)
	    @NotBlank(message = "firstName is mandatory")
	    private String firstName;

	    @Column(length = 100)
	    @NotBlank(message = "lastName is mandatory")
	    private String lastName;

	    @Column(length = 100)
	    private String email;

	    @Column(length = 100)
	    @NotBlank(message = "phoneNumber is mandatory")
	    private String phoneNumber;


	    @CreationTimestamp
	    private LocalDateTime createdDate;

	    @UpdateTimestamp
	    private LocalDateTime updatedDate;
	    
	

	    


}
