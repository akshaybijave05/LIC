package com.license.service;

import java.security.Principal;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import com.license.model.UserDtls;

public interface UserService {

	public UserDtls createUser(UserDtls user, String url);

	public boolean checkEmail(String email);

	public String updateProfile(UserDtls userDtls, Principal principal);

	public void deleteProfile(String email);

	public boolean verifyAccount(String code);

	List<UserDtls> getAllUsers();

	UserDtls getUserById(int id);

	UserDtls updateUser(int id, UserDtls updatedUser);

	void deleteUser(int id);

	UserDtls loginUser(String email, String password);

	String generateOtp(String email) throws MessagingException;

	void sendOtpEmail(String email, String otp) throws MessagingException;

	boolean verifyOtp(HttpSession session, String enteredOtp);

	Boolean updatePassword(long password, String currentUser);

	Boolean resetPassword(long currentPassword, String currentUser, long newPassword);

}