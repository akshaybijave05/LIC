package com.license.scheduler;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.license.model.UserDtls;
import com.license.service.EmailServieces;
import com.license.service.UserService;


@Service
public class PasswordResetScheduler {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailServieces emailService;

    public PasswordResetScheduler(UserService userService, EmailServieces emailService) {
		super();
		this.userService = userService;
		this.emailService = emailService;
	}

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public void startScheduler() {
        // Schedule the task to run every 5 year
    	scheduler.scheduleAtFixedRate(this::checkAndSendPasswordResetEmail, 0, 5, TimeUnit.MINUTES);

    }

    @PostConstruct
    public void initializeScheduler() {
        startScheduler();
    }
    
    
    private void checkAndSendPasswordResetEmail() {
        List<UserDtls> users = userService.getAllUsers();

        for (UserDtls user : users) {
            Date passwordUpdatedAt = user.getPasswordUpdatedAt();
            LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);

            System.out.println("user data checked ");
            
            if (passwordUpdatedAt != null && passwordUpdatedAt.toLocalDate().isBefore(threeMonthsAgo)) {
                // Password needs to be reset, send email
                sendPasswordResetEmail(user);
            }
        }
    }
    
    

    private void sendPasswordResetEmail(UserDtls user) {
        // Implement your logic to send a password reset email
        // You can use the emailService.sendPasswordResetEmail(user.getEmail()) method or any other logic
        try {
			emailService.sendEmailwithTemplate(user.getEmail(),"resetPasswordTemplate");
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
    }
}
