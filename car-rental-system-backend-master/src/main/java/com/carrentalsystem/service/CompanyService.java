package com.carrentalsystem.service;

import java.util.List;

import com.carrentalsystem.entity.Company;

public interface CompanyService {

	Company addCompany(Company company);

	Company updateCompany(Company company);

	Company getById(int companyId);
	
	List<Company> getAllCompany();

}
