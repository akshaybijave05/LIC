package com.license.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;



@Service
public class EmailServieces {

	@Autowired 
	JavaMailSender javaMailSender;
	
	@Autowired
	TemplateEngine templateEngine;
	
	public void sendEmailwithTemplate(String email, String filePath) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		Context context = new Context();
		context.setVariable("email", email);
		String emailContent = templateEngine.process(filePath, context);
		helper.setTo(email);
		helper.setSubject("update your password");
		helper.setText(emailContent, true);

		javaMailSender.send(message);

	}
}
