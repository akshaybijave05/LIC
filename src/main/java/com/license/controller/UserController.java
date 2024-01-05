package com.license.controller;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.license.model.UserDtls;
import com.license.repository.UserRepository;
import com.license.service.UserService;



@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncode;

	@Autowired
	private UserService userService;

	@ModelAttribute
	private void userDetails(Model m, Principal p) {
		String email = p.getName();
		UserDtls user = userRepo.findByEmail(email);

		m.addAttribute("user", user);
	}

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@GetMapping("/changPass")
	public String loadChangePassword() {
		return "user/change_password";
	}

	@PostMapping("/updatePassword")
	public String changePassword(Principal p, @RequestParam("oldPass") String oldPass,
			@RequestParam("newPass") String newPass, HttpSession session) {
		String email = p.getName();
		UserDtls loginUser = userRepo.findByEmail(email);
		boolean f = passwordEncode.matches(oldPass, loginUser.getPassword());

		if (f) {

			loginUser.setPassword(passwordEncode.encode(newPass));
			UserDtls updatePasswordUser = userRepo.save(loginUser);

			if (updatePasswordUser != null) {

				session.setAttribute("msg", "Password Changed Successfully");
			} else {

				session.setAttribute("msg", "Something Wrong On Server");

			}

		} else {

			session.setAttribute("msg", "Old Password Incorrect");

		}

		return "redirect:/user/changPass";
	}


	@GetMapping("/profile")
	public String profile() {
		return "user/profile";
	}
	@GetMapping("/editProfile")
	public String editProfile(ModelAndView ModelAndView, Principal p) {
		ModelAndView.addObject("user", userRepo.findByEmail(p.getName()));
		ModelAndView.setViewName("/user/editProfile");
		return "user/editProfile";
	}

	@PostMapping("/updateProfile")
	public String updateProfile(UserDtls userDtls, HttpSession session, Principal principal) {

		userService.updateProfile(userDtls, principal);
		// session.setAttribute("msg","Profile Successfully Updated..");

		return "user/home";

	}

	@GetMapping("/delete-profile")
	public String deleteProfile(Principal p) {

		userService.deleteProfile(p.getName());
		System.out.println(p.getName());
		return "index";
	}
	
	
	
	
	
	
	
	
	
	


//	@GetMapping("/getAllUsers")
//	public List<User> getAllUsers() {
//		return userService.getAllUsers();
//	}

	@GetMapping("/getAllUsers")
	public String getAllUsers(Model model) {
		List<UserDtls> users = userService.getAllUsers();
		model.addAttribute("users", users);
		return "usersList";
	}

	@GetMapping("/{id}")
	public UserDtls getUserById(@PathVariable int id) {
		return userService.getUserById(id);
	}

	

	


	@PostMapping("/loginUser")
	public ResponseEntity<User> loginUser(@RequestBody User loginCredentials, HttpServletRequest request) {
		String email = loginCredentials.getEmail();
		String password = loginCredentials.getPassword();

		User loggedInUser = userService.loginUser(email, password);

		if (loggedInUser != null) {
			// Store user information in the session
			HttpSession session = request.getSession();
			session.setAttribute("loggedInUser", loggedInUser);

			return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
		} else {
			// You can customize the response based on your requirements
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			System.out.println("user logged out " + session.getAttribute("loggedInUser"));
			session.invalidate(); // Invalidate the session

			return "login"; // Redirect to the login page or any other page
		}
		return "home";
	}


	// this is front page end-point

	@RequestMapping("/registration")
	public String registration() {

		return "createUser";
	}

	@RequestMapping("/loginUser")
	public String loginUser() {

		return "login";
	}

	@RequestMapping("/home")
	public String home() {

		return "home";

	}

	@RequestMapping("/forgotPage")
	public String forgotPage() {
		return "forgotPassword";

	}

	@RequestMapping("/updatePasswordPage")
	public String updatePasswordPage() {
		return "updatePassword";

	}

	@PostMapping("/forgot")
	public String generateOtp(@RequestParam("email") String email, HttpServletRequest request)
			throws MessagingException {
		request.getSession().setAttribute("email", email);
		userService.generateOtp(email);
		return "varifyPage";
	}

	@PostMapping("/verifyOtp")
	public String verifyOtp(@RequestParam("otp") String enteredOtp, HttpServletRequest request) {
		HttpSession session = request.getSession();
		System.out.print("the otp is : " + enteredOtp);

		boolean isOtpValid = userService.verifyOtp(session, enteredOtp);

		if (isOtpValid) {
			// OTP is valid, you can proceed with the desired action
			return "newPasswordPage";
		} else {
			// Invalid OTP, return an error response
			return "failed to set the password";
		}
	}

	@PostMapping("/updatePassword")
	public String resetPassword(HttpServletRequest request, @RequestParam("newpassword") long newPassword,
			@RequestParam("password") long password) {
		String currentUser = (String) request.getSession().getAttribute("email");
		if (newPassword == password) {
			Boolean isUpdated = userService.updatePassword(password, currentUser);
			if (isUpdated)
				return "login";
			else
				return "failed to update the password";
		} else
			return "password mismatch";
	}

	@PostMapping("/resetPassword")
	public String updatePassword(@RequestParam("email") String currentUser,
			@RequestParam("newpassword") long newPassword, @RequestParam("currentpassword") long currentPassword,
			@RequestParam("password") long confirmPassword) {

		if (newPassword == confirmPassword) {
			Boolean isUpdated = userService.resetPassword(currentPassword, currentUser, newPassword);
			if (isUpdated)
				return "login";
			else
				return "failed to update the password";
		} else
			return "password mismatch";
	}

	
	
	
	

}