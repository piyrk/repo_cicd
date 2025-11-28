package com.carrentalsystem.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.carrentalsystem.dto.AddDrivingLicenseRequest;
import com.carrentalsystem.dto.CommonApiResponse;
import com.carrentalsystem.dto.RegisterUserRequestDto;
import com.carrentalsystem.dto.UserDto;
import com.carrentalsystem.dto.UserLoginRequest;
import com.carrentalsystem.dto.UserLoginResponse;
import com.carrentalsystem.dto.UserResponseDto;
import com.carrentalsystem.dto.UserStatusUpdateRequestDto;
import com.carrentalsystem.entity.Address;
import com.carrentalsystem.entity.DrivingLicense;
import com.carrentalsystem.entity.User;
import com.carrentalsystem.exception.UserSaveFailedException;
import com.carrentalsystem.service.AddressService;
import com.carrentalsystem.service.DrivingLicenseService;
import com.carrentalsystem.service.StorageService;
import com.carrentalsystem.service.UserService;
import com.carrentalsystem.utility.Constants.ActiveStatus;
import com.carrentalsystem.utility.Constants.UserRole;
import com.carrentalsystem.utility.JwtUtils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Component
@Transactional
public class UserResource {

	private final Logger LOG = LoggerFactory.getLogger(UserResource.class);

	@Autowired
	private UserService userService;

	@Autowired
	private AddressService addressService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private StorageService storageService;

	@Autowired
	private DrivingLicenseService drivingLicenseService;

	public ResponseEntity<CommonApiResponse> registerAdmin(RegisterUserRequestDto registerRequest) {

		LOG.info("Request received for Register Admin");

		CommonApiResponse response = new CommonApiResponse();

		if (registerRequest == null) {
			response.setResponseMessage("user is null");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (registerRequest.getEmailId() == null || registerRequest.getPassword() == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> existingUsers = this.userService.getUserByEmailAndStatus(registerRequest.getEmailId(),
				ActiveStatus.ACTIVE.value());

		if (!existingUsers.isEmpty()) {
			response.setResponseMessage("User already register with this Email");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = RegisterUserRequestDto.toUserEntity(registerRequest);

		user.setRole(UserRole.ROLE_ADMIN.value());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setStatus(ActiveStatus.ACTIVE.value());

		User savedUser = this.userService.addUser(user);

		if (savedUser == null) {
			response.setResponseMessage("failed to register admin");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		response.setResponseMessage("Admin registered Successfully");
		response.setSuccess(true);

		LOG.info("Response Sent!!!");

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> registerUser(RegisterUserRequestDto request) {

		LOG.info("Received request for register user");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("user is null");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> existingUsers = this.userService.getUserByEmailAndStatus(request.getEmailId(), ActiveStatus.ACTIVE.value());

		if (!existingUsers.isEmpty()) {
			response.setResponseMessage("User with this Email Id already resgistered!!!");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getRole() == null) {
			response.setResponseMessage("bad request ,Role is missing");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = RegisterUserRequestDto.toUserEntity(request);

		String encodedPassword = passwordEncoder.encode(user.getPassword());

		user.setStatus(ActiveStatus.ACTIVE.value());
		user.setPassword(encodedPassword);

		Address address = new Address();
		address.setCity(request.getCity());
		address.setPincode(request.getPincode());
		address.setStreet(request.getStreet());

		Address savedAddress = this.addressService.addAddress(address);

		if (savedAddress == null) {
			throw new UserSaveFailedException("Registration Failed because of Technical issue:(");
		}

		user.setAddress(savedAddress);

		User savedUser = this.userService.addUser(user);

		if (savedUser == null) {
			throw new UserSaveFailedException("Registration Failed because of Technical issue:(");
		}

		response.setResponseMessage("User registered Successfully");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<UserLoginResponse> login(UserLoginRequest loginRequest) {

		LOG.info("Received request for User Login");

		UserLoginResponse response = new UserLoginResponse();

		if (loginRequest == null) {
			response.setResponseMessage("Missing Input");
			response.setSuccess(false);

			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String jwtToken = null;
		User user = null;

		List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority(loginRequest.getRole()));

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmailId(),
					loginRequest.getPassword(), authorities));
		} catch (Exception ex) {
			response.setResponseMessage("Invalid email or password.");
			response.setSuccess(false);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

		jwtToken = jwtUtils.generateToken(loginRequest.getEmailId());

		List<User> users = this.userService.getUserByEmailIdAndRoleAndStatus(loginRequest.getEmailId(), loginRequest.getRole(),
				ActiveStatus.ACTIVE.value());

		if (!users.isEmpty()) {
			user = users.get(0);
		}

		UserDto userDto = UserDto.toUserDtoEntity(user);

		// user is authenticated
		if (jwtToken != null) {
			response.setUser(userDto);
			response.setResponseMessage("Logged in sucessful");
			response.setSuccess(true);
			response.setJwtToken(jwtToken);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.OK);
		}

		else {
			response.setResponseMessage("Failed to login");
			response.setSuccess(false);
			return new ResponseEntity<UserLoginResponse>(response, HttpStatus.BAD_REQUEST);
		}

	}

	public ResponseEntity<UserResponseDto> getUsersByRole(String role) {

		UserResponseDto response = new UserResponseDto();

		if (role == null) {
			response.setResponseMessage("missing role");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> users = new ArrayList<>();

		users = this.userService.getUserByRoleAndStatus(role, ActiveStatus.ACTIVE.value());

		if (users.isEmpty()) {
			response.setResponseMessage("No Users Found");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
		}

		List<UserDto> userDtos = new ArrayList<>();

		for (User user : users) {

			UserDto dto = UserDto.toUserDtoEntity(user);

			userDtos.add(dto);

		}

		response.setUsers(userDtos);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> updateUserStatus(UserStatusUpdateRequestDto request) {

		LOG.info("Received request for updating the user status");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("bad request, missing data");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getUserId() == 0) {
			response.setResponseMessage("bad request, user id is missing");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = null;
		user = this.userService.getUserById(request.getUserId());

		user.setStatus(request.getStatus());

		User updatedUser = this.userService.updateUser(user);

		if (updatedUser == null) {
			throw new UserSaveFailedException("Failed to update the User status");
		}

		response.setResponseMessage("User " + request.getStatus() + " Successfully!!!");
		response.setSuccess(true);
		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);

	}

	public ResponseEntity<UserResponseDto> getUserById(int userId) {

		UserResponseDto response = new UserResponseDto();

		if (userId == 0) {
			response.setResponseMessage("Invalid Input");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.BAD_REQUEST);
		}

		List<User> users = new ArrayList<>();

		User user = this.userService.getUserById(userId);
		users.add(user);

		if (users.isEmpty()) {
			response.setResponseMessage("No Users Found");
			response.setSuccess(false);
			return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
		}

		List<UserDto> userDtos = new ArrayList<>();

		for (User u : users) {

			UserDto dto = UserDto.toUserDtoEntity(u);

			userDtos.add(dto);

		}

		response.setUsers(userDtos);
		response.setResponseMessage("User Fetched Successfully");
		response.setSuccess(true);

		return new ResponseEntity<UserResponseDto>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> deleteUserById(int userId) {

		CommonApiResponse response = new CommonApiResponse();

		if (userId == 0) {
			response.setResponseMessage("user id missing");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User user = this.userService.getUserById(userId);

		if (user == null) {
			response.setResponseMessage("User not found");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		user.setStatus(ActiveStatus.DEACTIVATED.value());

		User updatedUser = this.userService.updateUser(user);

		if (updatedUser == null) {
			response.setResponseMessage("Failed to Delete the User");
			response.setSuccess(false);
			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.setResponseMessage("User Deleted Successful!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CommonApiResponse> addCustomerDrivingLicense(AddDrivingLicenseRequest request) {

		LOG.info("Request received for Register Admin");

		CommonApiResponse response = new CommonApiResponse();

		if (request == null) {
			response.setResponseMessage("bad request - missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		if (request.getCustomerId() == null || request.getLicenseNumber() == null || request.getLicensePic() == null
				|| request.getExpirationDate() == null) {
			response.setResponseMessage("missing input");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		User customer = this.userService.getUserById(request.getCustomerId());

		if (customer == null) {
			response.setResponseMessage("Customer not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		String licensePicName = this.storageService.storeLicenseImage(request.getLicensePic());

		DrivingLicense license = new DrivingLicense();
		license.setExpirationDate(request.getExpirationDate());
		license.setLicenseNumber(request.getLicenseNumber());
		license.setLicensePic(licensePicName);

		DrivingLicense addedLicense = this.drivingLicenseService.addLicense(license);

		if (addedLicense == null) {
			throw new UserSaveFailedException("Failed to save the Driving license!!!");
		}

		customer.setLicense(addedLicense);

		User updatedCustomer = this.userService.updateUser(customer);

		if (updatedCustomer == null) {
			throw new UserSaveFailedException("Failed to save the Driving license!!!");
		}

		response.setResponseMessage("Customer Driving License Added successful!!!");
		response.setSuccess(true);

		LOG.info("Response Sent!!!");

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public void fetchDrivingLicenceImage(String drivingLicense, HttpServletResponse resp) {
		System.out.println("request came for fetching driving license pic");
		System.out.println("Loading file: " + drivingLicense);
		Resource resource = storageService.loadLicenseImage(drivingLicense);
		if (resource != null) {
			try (InputStream in = resource.getInputStream()) {
				ServletOutputStream out = resp.getOutputStream();
				FileCopyUtils.copy(in, out);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("response sent!");
	}

}
