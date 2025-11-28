package com.carrentalsystem;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.carrentalsystem.entity.User;
import com.carrentalsystem.service.UserService;
import com.carrentalsystem.utility.Constants.ActiveStatus;
import com.carrentalsystem.utility.Constants.UserRole;

@SpringBootApplication
public class CarRentalSystemApplication implements CommandLineRunner {
	
	private final Logger LOG = LoggerFactory.getLogger(CarRentalSystemApplication.class);
	
	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(CarRentalSystemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		List<User> admins = this.userService.getUserByEmailIdAndRoleAndStatus("demo.admin@demo.com",
				UserRole.ROLE_ADMIN.value(), ActiveStatus.ACTIVE.value());

		if (admins.isEmpty()) {

			LOG.info("Admin not found in system, so adding default admin");

			User user = new User();
			user.setEmailId("demo.admin@demo.com");
			user.setPassword(passwordEncoder.encode("123456"));
			user.setRole(UserRole.ROLE_ADMIN.value());
			user.setStatus(ActiveStatus.ACTIVE.value());

			this.userService.addUser(user);

		}
		
	}

}
