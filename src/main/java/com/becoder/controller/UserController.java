package com.becoder.controller;

import java.security.Principal;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.becoder.model.UserDtls;
import com.becoder.repository.UserRepository;
import com.becoder.service.UserService;

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

}