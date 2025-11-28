package com.carrentalsystem.service;

import com.carrentalsystem.entity.DrivingLicense;

public interface DrivingLicenseService {

	DrivingLicense addLicense(DrivingLicense drivingLicense);

	DrivingLicense updateLicense(DrivingLicense drivingLicense);

	DrivingLicense getById(int licenseId);

}
