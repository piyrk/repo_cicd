package com.carrentalsystem.resource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.carrentalsystem.dto.CommonApiResponse;
import com.carrentalsystem.dto.CompanyResponse;
import com.carrentalsystem.entity.Company;
import com.carrentalsystem.exception.CompanySaveFailedException;
import com.carrentalsystem.service.CompanyService;

@Component
public class CompanyResource {

	private final Logger LOG = LoggerFactory.getLogger(CompanyResource.class);

	@Autowired
	private CompanyService companyService;

	public ResponseEntity<CommonApiResponse> addCompany(Company company) {

		LOG.info("Request received for adding company");

		CommonApiResponse response = new CommonApiResponse();

		if (company == null || company.getName() == null) {
			response.setResponseMessage("bad request - invalid request");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Company addedCompany = this.companyService.addCompany(company);

		if (addedCompany == null) {
			throw new CompanySaveFailedException("Failed to save Company");
		}

		response.setResponseMessage("Company Added Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

	public ResponseEntity<CompanyResponse> fetchAllCompany() {

		LOG.info("Request received for fetching all company");

		CompanyResponse response = new CompanyResponse();

		List<Company> companies = this.companyService.getAllCompany();

		if (CollectionUtils.isEmpty(companies)) {
			response.setResponseMessage("No Companies Found!!!");
			response.setSuccess(false);

			return new ResponseEntity<CompanyResponse>(response, HttpStatus.OK);
		}

		response.setCompanies(companies);
		response.setResponseMessage("Companies Fetched Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CompanyResponse>(response, HttpStatus.OK);

	}
	
	public ResponseEntity<CommonApiResponse> updateCompany(Company company) {

		LOG.info("Request received for adding company");

		CommonApiResponse response = new CommonApiResponse();

		if (company == null || company.getId() == 0 || company.getName() == null) {
			response.setResponseMessage("bad request - invalid request");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}

		Company dbCompany = this.companyService.getById(company.getId());
		
		if(dbCompany == null) {
			response.setResponseMessage("bad request - Company not found");
			response.setSuccess(false);

			return new ResponseEntity<CommonApiResponse>(response, HttpStatus.BAD_REQUEST);
		}
		
		dbCompany.setName(company.getName());
		
		Company addedCompany = this.companyService.addCompany(dbCompany);

		if (addedCompany == null) {
			throw new CompanySaveFailedException("Failed to update Company");
		}

		response.setResponseMessage("Company Updated Successful!!!");
		response.setSuccess(true);

		return new ResponseEntity<CommonApiResponse>(response, HttpStatus.OK);
	}

}
