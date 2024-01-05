package com.license.service;

import java.security.Principal;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.license.model.Role;
import com.license.model.UserDtls;
import com.license.repository.RoleRepository;
import com.license.repository.UserRepository;

import net.bytebuddy.utility.RandomString;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncode;

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private  RoleRepository roleRepository;

	@Override
	public UserDtls createUser(UserDtls user, String url) {

		user.setPassword(passwordEncode.encode(user.getPassword()));
		Role role = new Role();
		role.setRoleName("ROLE_USER");
		roleRepository.save(role);
		user.setRoles(role);

		user.setEnabled(true);
		RandomString rn = new RandomString();
		user.setVerificationCode(rn.make(64));

		UserDtls us = userRepo.save(user);
		sendVerificationMail(user, url);
		return us;
	}

	@Override
	public boolean checkEmail(String email) {

		return userRepo.existsByEmail(email);
	}

	public void sendVerificationMail(UserDtls user, String url) {

		String from = "akshaybijave505@gmail.com";
		String to = user.getEmail();
		String subject = "Account Verification";
		String content = "Dear [[name]],<br>" + "Please click the link below to verify your registration:<br>"
		// + "and your company email address is: lalit12@newrise.com"
				+ "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>" + "Thank you,<br>"
				+ "Newrise Technosys Pvt.Ltd.";

		try {

			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message);

			helper.setFrom(from, "Newrise Technosys Pvt.Ltd.");
			helper.setTo(to);
			helper.setSubject(subject);

			content = content.replace("[[name]]", user.getFullName());

			String siteUrl = url + "/verify?code=" + user.getVerificationCode();

			content = content.replace("[[URL]]", siteUrl);

			helper.setText(content, true);

			mailSender.send(message);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean verifyAccount(String code) {

		UserDtls user = userRepo.findByVerificationCode(code);

		if (user != null) {

			user.setEnabled(true);
			user.setVerificationCode(null);
			userRepo.save(user);
			return true;
		}

		return false;
	}

	@Override
	public String updateProfile(UserDtls userDtls,Principal principal) {
		// TODO Auto-generated method stub

		
	   String name =	principal.getName();
		UserDtls user = userRepo.findByEmail(name);

		if (user == null) {
			return null;
		}
		UserDtls userDtls2 = user;
		userDtls2.setFullName(userDtls.getFullName());
		userDtls2.setEmail(userDtls.getEmail());
		userDtls2.setMobileNumber(userDtls.getMobileNumber());
		userRepo.save(userDtls2);
		return "";
	}

	@Override
	@Transactional
	public void deleteProfile(String email) {
		userRepo.deleteByEmail(email);

	}

}
