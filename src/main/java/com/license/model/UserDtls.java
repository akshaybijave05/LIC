package com.license.model;



import java.sql.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Data;

@Data
@Entity
public class UserDtls {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String fullName;

	@Column(unique = true)  // Ensure uniqueness in the database
	private String email;

	private String password;
	
	private Date passwordUpdatedAt ;
	  
	private String mobileNumber;
	
	private boolean accountNonLocked;
	
	private boolean enabled;
	
	private String verificationCode;
	
	 
	@ManyToOne
	@JoinColumn(name="role_id")
     Role  roles ;
     
	
	@OneToOne( cascade = CascadeType.ALL , fetch = FetchType.EAGER)
	private License license;
  
	
	
	
	
	
	

}