package com.carrentalsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrentalsystem.dto.CommonApiResponse;
import com.carrentalsystem.dto.CompanyResponse;
import com.carrentalsystem.entity.Company;
import com.carrentalsystem.resource.CompanyResource;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/company")
public class CompanyController {

	@Autowired
	private CompanyResource companyResource;

	@PostMapping("/add")
	@Operation(summary = "Api to add company")
	public ResponseEntity<CommonApiResponse> addCompany(@RequestBody Company company) {
		return companyResource.addCompany(company);
	}

	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch all company")
	public ResponseEntity<CompanyResponse> fetchAllCompany() {
		return companyResource.fetchAllCompany();
	}

	@PutMapping("/udpate")
	@Operation(summary = "Api to update company")
	public ResponseEntity<CommonApiResponse> updateCompany(@RequestBody Company company) {
		return companyResource.updateCompany(company);
	}

}
