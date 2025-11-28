package com.carrentalsystem.service;

import java.util.List;

import com.carrentalsystem.entity.Company;
import com.carrentalsystem.entity.Variant;

public interface VariantService {

	Variant addVariant(Variant variant);

	Variant updateVariant(Variant variant);

	Variant getById(int variantId);

	List<Variant> getByCompany(Company company);

	List<Variant> getByCompanyAndStatus(Company company, String status);

	List<Variant> getByStatus(String status);

	List<Variant> searchByVariants(String variantName, String status);

}
