package com.carrentalsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrentalsystem.dto.CommonApiResponse;
import com.carrentalsystem.dto.VariantAddRequest;
import com.carrentalsystem.dto.VariantResponse;
import com.carrentalsystem.resource.VariantResource;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("api/variant")
public class VariantController {

	@Autowired
	private VariantResource variantResource;

	@PostMapping("/add")
	@Operation(summary = "Api to add variant")
	public ResponseEntity<CommonApiResponse> addVariant(VariantAddRequest request) {
		return variantResource.addVariant(request);
	}

	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch all active variant")
	public ResponseEntity<VariantResponse> fetchAllCompany() {
		return variantResource.fetchAllVariant();
	}

	@GetMapping("/fetch")
	@Operation(summary = "Api to fetch variant by id")
	public ResponseEntity<VariantResponse> fetchVariantByID(@RequestParam("variantId") int variantId) {
		return variantResource.fetchVariantByID(variantId);
	}

	@GetMapping("/fetch/company-wise")
	@Operation(summary = "Api to fetch variants by company-wise")
	public ResponseEntity<VariantResponse> fetchVariantsByCompany(@RequestParam("companyId") int companyId) {
		return variantResource.fetchVariantsByCompany(companyId);
	}

	@GetMapping("/search")
	@Operation(summary = "Api to fetch ")
	public ResponseEntity<VariantResponse> searchVariants(@RequestParam("variantName") String variantName) {
		return variantResource.searchVariants(variantName);
	}
	
	@GetMapping(value = "/{variantImage}", produces = "image/*")
	@Operation(summary = "Api to fetch variant image by using image name")
	public void fetchProductImage(@PathVariable("variantImage") String variantImage, HttpServletResponse resp) {
		this.variantResource.fetchVariantImage(variantImage, resp);
	}
	
	@PutMapping("/update")
	@Operation(summary = "Api to add variant")
	public ResponseEntity<CommonApiResponse> updateVariant(VariantAddRequest request) {
		return variantResource.updateVariant(request);
	}
	
	@DeleteMapping("/delete")
	@Operation(summary = "Api to delete the variant")
	public ResponseEntity<CommonApiResponse> deleteVariant(@RequestParam("variantId") int variantId) {
		return variantResource.deleteVariant(variantId);
	}

}
