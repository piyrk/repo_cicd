package com.carrentalsystem.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrentalsystem.entity.Variant;
import com.carrentalsystem.entity.Vehicle;

@Repository
public interface VehicleDao extends JpaRepository<Vehicle, Integer> {

	List<Vehicle> findByVariantAndStatus(Variant variant, String status);
	
	List<Vehicle> findByStatus(String status);

}
