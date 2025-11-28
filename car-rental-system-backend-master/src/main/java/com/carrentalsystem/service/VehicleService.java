package com.carrentalsystem.service;

import java.util.List;

import com.carrentalsystem.entity.Variant;
import com.carrentalsystem.entity.Vehicle;

public interface VehicleService {

	Vehicle addVehicle(Vehicle vehicle);

	Vehicle updateVehicle(Vehicle vehicle);

	Vehicle getById(int vehicleId);

	List<Vehicle> getByVariantAndStatus(Variant variant, String status);
	
	List<Vehicle> getByStatus(String status);

}
