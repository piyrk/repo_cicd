package com.carrentalsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrentalsystem.resource.HelperResource;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/car/rental/helper")
public class HelperController {
	
	@Autowired
	private HelperResource helperResource;
	
	@GetMapping("/fetch/fuel-type")
	@Operation(summary = "Api to fetch all fuel type")
	public ResponseEntity fetchAllFuelType() {
		return helperResource.fetchAllFuelType();
	}

}
