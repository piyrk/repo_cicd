package com.carrentalsystem.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrentalsystem.entity.DrivingLicense;

@Repository
public interface DrivingLicenseDao extends JpaRepository<DrivingLicense, Integer>{

}
