package com.license.model;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class UserDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String fullName;

	private String email;

	private String password;
	  
	private String mobileNumber;
	
	private boolean accountNonLocked;
	
	private boolean enabled;
	
	private String verificationCode;
	
	 
	@ManyToOne
	@JoinColumn(name="role_id")
     Role  roles ;
     
	

}