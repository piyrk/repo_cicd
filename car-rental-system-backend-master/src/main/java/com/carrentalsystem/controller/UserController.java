package com.carrentalsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrentalsystem.dto.AddDrivingLicenseRequest;
import com.carrentalsystem.dto.CommonApiResponse;
import com.carrentalsystem.dto.RegisterUserRequestDto;
import com.carrentalsystem.dto.UserLoginRequest;
import com.carrentalsystem.dto.UserLoginResponse;
import com.carrentalsystem.dto.UserResponseDto;
import com.carrentalsystem.dto.UserStatusUpdateRequestDto;
import com.carrentalsystem.resource.UserResource;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/user")
public class UserController {

	@Autowired
	private UserResource userResource;

	// RegisterUserRequestDto, we will set only email, password & role from UI
	@PostMapping("/admin/register")
	@Operation(summary = "Api to register Admin")
	public ResponseEntity<CommonApiResponse> registerAdmin(@RequestBody RegisterUserRequestDto request) {
		return userResource.registerAdmin(request);
	}

	@PostMapping("register")
	@Operation(summary = "Api to register customer or seller user")
	public ResponseEntity<CommonApiResponse> registerUser(@RequestBody RegisterUserRequestDto request) {
		return this.userResource.registerUser(request);
	}

	@PostMapping("login")
	@Operation(summary = "Api to login any User")
	public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
		return userResource.login(userLoginRequest);
	}

	@GetMapping("/fetch/role-wise")
	@Operation(summary = "Api to get Users By Role")
	public ResponseEntity<UserResponseDto> fetchAllUsersByRole(@RequestParam("role") String role)
			throws JsonProcessingException {
		return userResource.getUsersByRole(role);
	}

	@PutMapping("update/status")
	@Operation(summary = "Api to update the user status")
	public ResponseEntity<CommonApiResponse> updateUserStatus(@RequestBody UserStatusUpdateRequestDto request) {
		return userResource.updateUserStatus(request);
	}

	@GetMapping("/fetch/user-id")
	@Operation(summary = "Api to get User Detail By User Id")
	public ResponseEntity<UserResponseDto> fetchUserById(@RequestParam("userId") int userId) {
		return userResource.getUserById(userId);
	}

	@DeleteMapping("/delete/user-id")
	@Operation(summary = "Api to delete the user by ID")
	public ResponseEntity<CommonApiResponse> deleteUserById(@RequestParam("userId") int userId) {
		return userResource.deleteUserById(userId);
	}

	@PostMapping("add/driving-licence")
	@Operation(summary = "Api to add customer driving license")
	public ResponseEntity<CommonApiResponse> addCustomerDrivingLicense(AddDrivingLicenseRequest request) {
		return userResource.addCustomerDrivingLicense(request);
	}
	
	@GetMapping(value = "/{drivingLicense}", produces = "image/*")
	@Operation(summary = "Api to fetch driving license by using image name")
	public void fetchProductImage(@PathVariable("drivingLicense") String drivingLicense, HttpServletResponse resp) {
		this.userResource.fetchDrivingLicenceImage(drivingLicense, resp);
	}

}
