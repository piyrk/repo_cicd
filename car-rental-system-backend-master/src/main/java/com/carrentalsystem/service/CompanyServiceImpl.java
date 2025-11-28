package com.carrentalsystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carrentalsystem.dao.CompanyDao;
import com.carrentalsystem.entity.Company;

@Service
public class CompanyServiceImpl implements CompanyService {

	@Autowired
	private CompanyDao companyDao;

	@Override
	public Company addCompany(Company company) {
		// TODO Auto-generated method stub
		return companyDao.save(company);
	}

	@Override
	public Company updateCompany(Company company) {
		// TODO Auto-generated method stub
		return companyDao.save(company);
	}

	@Override
	public Company getById(int companyId) {
		Optional<Company> optional = companyDao.findById(companyId);

		if (optional.isPresent()) {
			return optional.get();
		}

		return null;
	}

	@Override
	public List<Company> getAllCompany() {
		// TODO Auto-generated method stub
		return companyDao.findAll();
	}

}
