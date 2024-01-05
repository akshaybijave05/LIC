package com.license.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.license.model.UserDtls;

public interface UserRepository extends JpaRepository<UserDtls, Integer> {

	public boolean existsByEmail(String email);

	public UserDtls findByEmail(String email);
	
	public UserDtls findByEmailAndMobileNumber(String email,String mobileNum);

	public UserDtls findByVerificationCode(String code);

	public void deleteByEmail(String email);	
	
	
	
	UserDtls findByEmailAndPassword(String email, String password);

	UserDtls findByEmail(String currentUser);


	
}