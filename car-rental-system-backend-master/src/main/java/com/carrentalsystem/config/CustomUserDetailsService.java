package com.carrentalsystem.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.carrentalsystem.entity.User;
import com.carrentalsystem.service.UserService;
import com.carrentalsystem.utility.Constants.ActiveStatus;

@Component
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		java.util.List<User> users = this.userService.getUserByEmailAndStatus(email, ActiveStatus.ACTIVE.value());

		if (users.isEmpty()) {
			throw new UsernameNotFoundException("User not found");
		}

		User user = users.get(0);

		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		return customUserDetails;

	}
}
