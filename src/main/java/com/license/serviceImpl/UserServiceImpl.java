package com.license.serviceImpl;

import java.security.Principal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.license.model.Role;
import com.license.model.UserDtls;
import com.license.repository.RoleRepository;
import com.license.repository.UserRepository;
import com.license.service.UserService;


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
		
		Date date = Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
		user.setPasswordUpdatedAt(date);

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

	
	
	
	
	
	
	
	
	
	
	
	
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private final Map<String, String> otpCache = new HashMap<>();

	@Override
	public UserDtls getUserById(int id) {
		return userRepository.findById(id).orElse(null);
	}

	private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor();

	public UserServiceImpl(UserRepository userRepository, JavaMailSender javaMailSender, TemplateEngine templateEngine,
			HttpServletResponse httpServletResponse) {
		super();
		this.userRepository = userRepository;
		this.javaMailSender = javaMailSender;
		this.templateEngine = templateEngine;

	}

	

	@Override
	public User loginUser(String email, String password) {
		return userRepository.findByEmailAndPassword(email, password);
	}


	@Override
	public List<UserDtls> getAllUsers() {
		return userRepository.findAll();

	}

	@Override
	public String generateOtp(String email) throws MessagingException {
		Random random = new Random();
		String otp = String.format("%04d", random.nextInt(10000));

		otpCache.put(email, otp);

		// Send the OTP via email
		sendOtpEmail(email, otp);

		cleanupExecutor.schedule(() -> otpCache.remove(email), 10, TimeUnit.MINUTES);

		return otp;
	}

	public void sendOtpEmail(String email, String otp) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		Context context = new Context();
		context.setVariable("otp", otp);
		context.setVariable("email", email);
		String emailContent = templateEngine.process("forgot_password_template.html", context);
		helper.setTo(email);
		helper.setSubject("forgot password otp");
		helper.setText(emailContent, true);

		javaMailSender.send(message);

	}

	@Override
	public boolean verifyOtp(HttpSession session, String enteredOtp) {
		String storedOtp = otpCache.get(session.getAttribute("email"));
		return storedOtp != null && storedOtp.equals(enteredOtp);
	}

	@Override
	public Boolean updatePassword(long password, String currentUser) {
		User user = userRepository.findByEmail(currentUser);
		String pwd = String.valueOf(password);
		if (user != null) {
			user.setPassword(pwd);
			Date date = Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
			user.setPasswordUpdatedAt(date);
		}
		User savedUser = userRepository.save(user);
		if (savedUser != null)
			return true;
		else {
			return false;
		}
	}

	@Override
	public Boolean resetPassword(long currentPassword, String currentUser, long newPassword) {
		String currentpwd = String.valueOf(currentPassword);
		User user = userRepository.findByEmailAndPassword(currentUser, currentpwd);
		String pwd = String.valueOf(newPassword);
		if (user != null) {
			user.setPassword(pwd);
			Date date = Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
			user.setPasswordUpdatedAt(date);
		}
		User savedUser = userRepository.save(user);
		if (savedUser != null)
			return true;
		else {
			return false;
		}
	}
}
