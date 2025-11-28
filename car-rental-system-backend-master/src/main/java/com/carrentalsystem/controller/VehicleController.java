package com.carrentalsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carrentalsystem.dto.AddVehicleRequest;
import com.carrentalsystem.dto.CommonApiResponse;
import com.carrentalsystem.dto.VehicleResponse;
import com.carrentalsystem.resource.VehicleResource;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("api/vehicle")
public class VehicleController {

	@Autowired
	private VehicleResource vehicleResource;

	@PostMapping("/add")
	@Operation(summary = "Api to add vehicle")
	public ResponseEntity<CommonApiResponse> addVehicle(@RequestBody AddVehicleRequest request) {
		return vehicleResource.addVehicle(request);
	}

	@GetMapping("/fetch/all")
	@Operation(summary = "Api to fetch all company")
	public ResponseEntity<VehicleResponse> fetchAllVehicles() {
		return vehicleResource.fetchAllVehicles();
	}

	@GetMapping("/fetch/variant-wise")
	@Operation(summary = "Api to fetch vehicles by variant")
	public ResponseEntity<VehicleResponse> fetchAllVehiclesByVariant(@RequestParam("variantId") Integer variantId) {
		return vehicleResource.fetchAllVehiclesByVariant(variantId);
	}

	@PutMapping("/udpate")
	@Operation(summary = "Api to update vehicle")
	public ResponseEntity<CommonApiResponse> updateVehicle(@RequestBody AddVehicleRequest request) {
		return vehicleResource.updateVehicle(request);
	}

	@DeleteMapping("/delete")
	@Operation(summary = "Api to delete vehicle")
	public ResponseEntity<CommonApiResponse> deleteVehicle(@RequestParam("vehicleId") Integer vehicleId) {
		return vehicleResource.deleteVehicle(vehicleId);
	}

}
