package com.license.service;

import java.security.Principal;

import com.license.model.UserDtls;

public interface UserService {


	public UserDtls createUser(UserDtls user,String url);

	public boolean checkEmail(String email);
	
	public String updateProfile(UserDtls userDtls ,Principal principal);
	
	public void deleteProfile(String email);
	
	public boolean verifyAccount(String code); 
}